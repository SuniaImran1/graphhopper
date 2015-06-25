package com.graphhopper.reader.osgb;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import com.graphhopper.reader.RoutingElement;
import com.graphhopper.reader.pbf.Sink;

/**
 * Abstract base class for reading in input files from xml, gz etc
 *
 * @author phopkins
 *
 * @param <T extends RoutingElement> The type of object to return from the next xml functions
 */
abstract public class AbstractOsInputFile<T extends RoutingElement>  implements Sink, Closeable {
    private boolean eof;
    private final InputStream bis;
    // for xml parsing
    protected XMLStreamReader parser;
    // for pbf parsing
    private boolean binary = false;
    private final BlockingQueue<RoutingElement> itemQueue;
    private final String name;
    private String idKey;
    /**
     * The factory to use to create RoutingElements. eg Itn, Dpn or Hn
     */
    private AbstractRoutingElementFactory<T> abstractFactory;

    public AbstractOsInputFile(File file, AbstractRoutingElementFactory<T> abstractFactory) throws IOException {
        this(file, abstractFactory, "id");
    }
    public AbstractOsInputFile(File file, AbstractRoutingElementFactory<T> abstractFactory, String idKey) throws IOException {
        this.idKey = idKey;
        this.abstractFactory = abstractFactory;
        name = file.getAbsolutePath();
        bis = decode(file);
        itemQueue = new LinkedBlockingQueue<RoutingElement>(50000);
    }

    public AbstractOsInputFile<T> open() throws XMLStreamException {
        openXMLStream(bis);
        return this;
    }

    public InputStream getInputStream() {
        return bis;
    }

    public AbstractRoutingElementFactory<T> getAbstractFactory() {
        return abstractFactory;
    }

    public void setAbstractFactory(AbstractRoutingElementFactory<T> abstractFactory) {
        this.abstractFactory = abstractFactory;
    }

    /**
     * Currently on for pbf format. Default is number of cores.
     */
    public AbstractOsInputFile<T> setWorkerThreads(int num) {
        //        workerThreads = num;
        return this;
    }

    @SuppressWarnings("unchecked")
    private InputStream decode(File file) throws IOException {
        final String name = file.getName();

        InputStream ips = null;
        try {
            ips = new BufferedInputStream(new FileInputStream(file), 50000);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ips.mark(10);

        // check file header
        byte header[] = new byte[6];
        ips.read(header);

        /*
         * can parse bz2 directly with additional lib if (header[0] == 'B' &&
         * header[1] == 'Z') { return new CBZip2InputStream(ips); }
         */
        if (header[0] == 31 && header[1] == -117) {
            ips.reset();
            return new GZIPInputStream(ips, 50000);
        } else if (header[0] == 0 && header[1] == 0 && header[2] == 0
                && header[4] == 10 && header[5] == 9
                && (header[3] == 13 || header[3] == 14)) {
            ips.reset();
            binary = true;
            return ips;
        } else if (header[0] == 'P' && header[1] == 'K') {
            ips.reset();
            ZipInputStream zip = new ZipInputStream(ips);
            zip.getNextEntry();

            return zip;
        } else if (name.endsWith(".gml") || name.endsWith(".xml")) {
            ips.reset();
            return ips;
        } else if (header[0] == 60 && header[1] == 63 && header[3] == 120
                && header[4] == 109 && header[5] == 108) {
            ips.reset();
            return ips;
        } else if (name.endsWith(".bz2") || name.endsWith(".bzip2")) {
            String clName = "org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream";
            try {
                Class clazz = Class.forName(clName);
                ips.reset();
                Constructor<InputStream> ctor = clazz.getConstructor(
                        InputStream.class, boolean.class);
                return ctor.newInstance(ips, true);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot instantiate "
                        + clName, e);
            }
        } else {
            throw new IllegalArgumentException(
                    "Input file is not of valid type " + file.getPath());
        }
    }

    private void openXMLStream(InputStream in) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        parser = factory.createXMLStreamReader(bis, "UTF-8");
        int event;
        do {
            event = parser.next();
        } while (event == XMLStreamConstants.COMMENT);

        if (event != XMLStreamConstants.START_ELEMENT
                || !parser.getLocalName().equalsIgnoreCase("FeatureCollection")) {
            throw new IllegalArgumentException(String.format(
                    "File %s not a valid OS ITN stream", name));
        }

        eof = false;
    }

    public T getNext() throws XMLStreamException,
    MismatchedDimensionException, FactoryException, TransformException {
        if (eof)
            throw new IllegalStateException("EOF reached");

        T item;
        item = getNextXML();

        if (item != null)
            return item;

        eof = true;
        return null;
    }

    //    private T getNextXML() throws XMLStreamException,
    //    MismatchedDimensionException, FactoryException, TransformException;

    private T getNextXML() throws XMLStreamException,
    MismatchedDimensionException, FactoryException, TransformException {
        T result = null;
        int event = parser.next();
        while (event != XMLStreamConstants.END_DOCUMENT) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                String idStr = parser.getAttributeValue(null, idKey);
                if (idStr != null) {
                    String name = parser.getLocalName();
                    //                    idStr = idStr.substring(4);
                    result = abstractFactory.create(name, idStr, parser);
                    if (result!=null) {
                        return result;
                    }
                }
            }
            event = parser.next();
        }
        parser.close();
        return result;
    }


    public boolean isEOF() {
        return eof;
    }

    @Override
    public void close() throws IOException {
        try {
            if (!binary && null != parser)
            		parser.close();
        } catch (XMLStreamException ex) {
            throw new IOException(ex);
        } finally {
            eof = true;
            bis.close();
        }
    }

    @Override
    public void process(RoutingElement item) {
        try {
            // blocks if full
            itemQueue.put(item);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        // throw exception if full
        // itemQueue.add(item);
    }

    @Override
    public void complete() {
        //        hasIncomingData = false;
    }
}