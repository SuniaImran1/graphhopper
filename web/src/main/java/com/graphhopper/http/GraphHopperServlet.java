/*
 *  Licensed to GraphHopper and Peter Karich under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.http;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.http.validation.BooleanValidator;
import com.graphhopper.http.validation.CaseInsensitiveStringListValidator;
import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.util.AbstractAvoidanceDecorator;
import com.graphhopper.routing.util.AbstractFlagEncoder;
import com.graphhopper.routing.util.EncoderDecorator;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.WeightingMap;
import com.graphhopper.util.Helper;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;
import com.graphhopper.util.TranslationMap;
import com.graphhopper.util.shapes.BBox;
import com.graphhopper.util.shapes.GHPoint;

/**
 * Servlet to use GraphHopper in a remote client application like mobile or browser. Note: If type
 * is json it returns the points in GeoJson format (longitude,latitude) unlike the format "lat,lon"
 * used otherwise. See the full API response format in docs/web/api-doc.md
 * <p/>
 *
 * @author Peter Karich
 */
public class GraphHopperServlet extends GHBaseServlet
{
	@Inject
	private GraphHopper hopper;

	@Named("internalErrorsAllowed")
	private boolean internalErrorsAllowed;

	@Override
	public void doGet( HttpServletRequest httpReq, HttpServletResponse httpRes )
			throws ServletException, IOException
	{

		boolean writeGPX = "gpx".equalsIgnoreCase(getParam(httpReq, "type", "json"));
		double minPathPrecision = getDoubleParam(httpReq, "way_point_max_distance", 1d);
		boolean enableInstructions = writeGPX || getBooleanParam(httpReq, "instructions", true);
		boolean calcPoints = getBooleanParam(httpReq, "calc_points", true);
		boolean enableElevation = getBooleanParam(httpReq, "elevation", false);
		boolean pointsEncoded = getBooleanParam(httpReq, "points_encoded", true);

		String vehicleStr = getParam(httpReq, "vehicle", null);
		String weighting = getParam(httpReq, "weighting", "fastest");
		String algoStr = getParam(httpReq, "algorithm", null);
		String localeStr = getParam(httpReq, "locale", "en_GB").replace('-', '_');

		StopWatch sw = new StopWatch().start();
		GHResponse ghRsp = null;

		String instructionsString = getParam(httpReq, "instructions", "true");
		String pointsEncodedString = getParam(httpReq, "points_encoded", "true");
		String calcPointsString = getParam(httpReq, "calc_points", "true");
		String debugString = getParam(httpReq, "debug", "true");
		String prettyString = getParam(httpReq, "pretty", "true");
		String avoidancesString = getParam(httpReq, "avoidances", null);
		List<GHPoint> infoPoints = null;
		try
		{
			ApiResource.ROUTE.checkAllRequestParameters(httpReq);

			infoPoints = getPoints(httpReq, "point");

			// we can reduce the path length based on the maximum differences to the original
			// coordinates

			if (!new CaseInsensitiveStringListValidator()
			.isValid(localeStr, TranslationMap.LOCALES))
			{
				String errMesg = buildErrorMessageString(localeStr, "locale",
						TranslationMap.LOCALES);
				ghRsp = new GHResponse()
				        .addError(new InvalidParameterException(errMesg.toString()));
			} else if (null != algoStr
			        && !new CaseInsensitiveStringListValidator().isValid(algoStr,
			                AlgorithmOptions.ASTAR, AlgorithmOptions.ASTAR_BI,
			                AlgorithmOptions.DIJKSTRA, AlgorithmOptions.DIJKSTRA_BI,
			                AlgorithmOptions.DIJKSTRA_ONE_TO_MANY))
			{
				String errMesg = buildErrorMessageString(algoStr, "algorithm",
						AlgorithmOptions.ASTAR, AlgorithmOptions.ASTAR_BI,
						AlgorithmOptions.DIJKSTRA, AlgorithmOptions.DIJKSTRA_BI,
						AlgorithmOptions.DIJKSTRA_ONE_TO_MANY);
				ghRsp = new GHResponse().addError(new InvalidParameterException(errMesg));
			} else if (!new BooleanValidator().isValid(instructionsString))
			{
				String errMesg = buildBooleanErrorMessageString(instructionsString, "instructions");
				ghRsp = new GHResponse().addError(new InvalidParameterException(errMesg));
			} else if (!new BooleanValidator().isValid(pointsEncodedString))
			{
				String errMesg = buildBooleanErrorMessageString(pointsEncodedString,
						"points_encoded");
				ghRsp = new GHResponse().addError(new InvalidParameterException(errMesg));
			} else if (!new BooleanValidator().isValid(calcPointsString))
			{
				String errMesg = buildBooleanErrorMessageString(calcPointsString, "calc_points");
				ghRsp = new GHResponse().addError(new InvalidParameterException(errMesg));
			} else if (!new BooleanValidator().isValid(debugString))
			{
				String errMesg = buildBooleanErrorMessageString(debugString, "debug");
				ghRsp = new GHResponse().addError(new InvalidParameterException(errMesg));
			} else if (!new BooleanValidator().isValid(prettyString))
			{
				String errMesg = buildBooleanErrorMessageString(prettyString, "pretty");
				ghRsp = new GHResponse().addError(new InvalidParameterException(errMesg));
			} else if (!hopper.getEncodingManager().supports(vehicleStr))
			{
				String supported = hopper.getGraph().getEncodingManager().toString();
				String errMesg = String.format(
						"Vehicle %s is not a valid vehicle. Valid vehicles are %s", vehicleStr,
						supported);
				ghRsp = new GHResponse().addError(new InvalidParameterException(errMesg));
			} else if (enableElevation && !hopper.hasElevation())
			{
				ghRsp = new GHResponse().addError(new InvalidParameterException(
						"Elevation not supported!"));
			} else
			{
				FlagEncoder algoVehicle = hopper.getEncodingManager().getEncoder(vehicleStr);

				// Lots of lovely braces. I will tidy this up next week... promise!
				if (avoidancesString != null)
				{
					List<String> allowedAvoidances = new ArrayList<>();
					// Check Avoidances
					if (algoVehicle instanceof AbstractFlagEncoder)
					{
						AbstractFlagEncoder abstractFlagEncoder = (AbstractFlagEncoder) algoVehicle;
						List<EncoderDecorator> encoderDecorators = abstractFlagEncoder
								.getEncoderDecorators();
						if (encoderDecorators != null)
						{
							for (EncoderDecorator encoderDecorator : encoderDecorators)
							{
								if (encoderDecorator instanceof AbstractAvoidanceDecorator)
								{
									AbstractAvoidanceDecorator abstractAvoidanceDecorator = (AbstractAvoidanceDecorator) encoderDecorator;
									allowedAvoidances.addAll(Arrays
											.asList(abstractAvoidanceDecorator
													.getEdgeAttributesOfInterestNames()));
								}
							}
						}
					}
					String avoidanceArray[] = avoidancesString.split(",");
					for (String avoidance : avoidanceArray)
					{
						if (!allowedAvoidances.contains(avoidance.trim()))
						{
							String errMesg = buildErrorMessageString(avoidance, "avoidances",
							        allowedAvoidances);
							ghRsp = new GHResponse().addError(new InvalidParameterException(errMesg
							        .toString()));
						}
					}
				}

				if (ghRsp == null)
				{
					GHRequest request = new GHRequest(infoPoints);

					initHints(request, httpReq.getParameterMap());
					request.setVehicle(algoVehicle.toString()).setWeighting(weighting)
					.setAlgorithm(algoStr).setLocale(localeStr).getHints()
					.put("calcPoints", calcPoints).put("instructions", enableInstructions)
					.put("wayPointMaxDistance", minPathPrecision);
					ghRsp = hopper.route(request);
				}
			}
		} catch (NoSuchParameterException | MissingParameterException | InvalidParameterException e)
		{
			ghRsp = new GHResponse().addError(e);
		}
		float took = sw.stop().getSeconds();
		String infoStr = httpReq.getRemoteAddr() + " " + httpReq.getLocale() + " "
				+ httpReq.getHeader("User-Agent");
		String logStr = httpReq.getQueryString() + " " + infoStr + " " + infoPoints + ", took:"
				+ took + ", " + algoStr + ", " + weighting + ", " + vehicleStr;

		if (ghRsp.hasErrors())
			logger.error(logStr + ", errors:" + ghRsp.getErrors());
		else
			logger.info(logStr + ", distance: " + ghRsp.getDistance() + ", time:"
					+ Math.round(ghRsp.getTime() / 60000f) + "min, points:"
					+ ghRsp.getPoints().getSize() + ", debug - " + ghRsp.getDebugInfo());

		if (writeGPX)
		{
			String xml = createGPXString(httpReq, httpRes, ghRsp);
			if (ghRsp.hasErrors())
			{
				httpRes.setStatus(SC_BAD_REQUEST);
				httpRes.getWriter().append(xml);
			} else
				writeResponse(httpRes, xml);
		} else
		{
			String type = getParam(httpReq, "type", "json");
			if (!"json".equalsIgnoreCase(type) || (!"jsonp".equalsIgnoreCase(type) && jsonpAllowed))
			{
				String errorMessage = type
						+ " is not a valid value for parameter type. Valid values are ";
				errorMessage += jsonpAllowed ? "JSON, GPX or JSONP." : "GPX or JSON.";
				ghRsp.addError(new InvalidParameterException(errorMessage));
			}
			Map<String, Object> map = createJson(ghRsp, calcPoints, pointsEncoded, enableElevation,
					enableInstructions);
			Object infoMap = map.get("info");
			if (infoMap != null)
				((Map) infoMap).put("took", Math.round(took * 1000));

			if (ghRsp.hasErrors())
			{
				writeJsonError(httpRes, SC_BAD_REQUEST, new JSONObject(map));
			} else
				writeJson(httpReq, httpRes, new JSONObject(map));
		}
	}

	private String buildBooleanErrorMessageString( String paramValue, String paramName )
	{
		return buildErrorMessageString(paramValue, paramName,
				Arrays.asList(new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString() }));
	}

	private String buildErrorMessageString( String paramValue, String paramName,
			String... validValues )
	{
		return buildErrorMessageString(paramValue, paramName, Arrays.asList(validValues));
	}

	private String buildErrorMessageString( String paramValue, String paramName,
			List<String> validValues )
	{
		StringBuilder errMesg = new StringBuilder(paramValue)
		.append(" is not a valid value for parameter ").append(paramName)
		.append(". Valid values are ");
		for (int i = 0; i < validValues.size(); i++)
		{
			String validStr = validValues.get(i);
			if (i == validValues.size() - 1)
			{
				errMesg.append(" or ");
			}
			errMesg.append(validStr);
			if (i < validValues.size() - 2)
			{
				errMesg.append(", ");
			}
		}
		return errMesg.toString();
	}

	protected String createGPXString( HttpServletRequest req, HttpServletResponse res,
			GHResponse rsp )
	{
		boolean includeElevation = getBooleanParam(req, "elevation", false);
		res.setCharacterEncoding("UTF-8");
		res.setContentType("application/xml");
		String trackName = getParam(req, "track", "GraphHopper Track");
		res.setHeader("Content-Disposition", "attachment;filename=" + "GraphHopper.gpx");
		long time = getLongParam(req, "millis", System.currentTimeMillis());
		if (rsp.hasErrors())
			return errorsToXML(rsp.getErrors());
		else
			return rsp.getInstructions().createGPX(trackName, time, includeElevation);
	}

	String errorsToXML( List<Throwable> list )
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element gpxElement = doc.createElement("gpx");
			gpxElement.setAttribute("creator", "GraphHopper");
			gpxElement.setAttribute("version", "1.1");
			doc.appendChild(gpxElement);

			Element mdElement = doc.createElement("metadata");
			gpxElement.appendChild(mdElement);

			Element extensionsElement = doc.createElement("extensions");
			mdElement.appendChild(extensionsElement);

			Element messageElement = doc.createElement("message");
			extensionsElement.appendChild(messageElement);
			messageElement.setTextContent(list.get(0).getMessage());

			Element hintsElement = doc.createElement("hints");
			extensionsElement.appendChild(hintsElement);

			for (Throwable t : list)
			{
				Element error = doc.createElement("error");
				hintsElement.appendChild(error);
				error.setAttribute("message", t.getMessage());
				if (internalErrorsAllowed)
				{
					error.setAttribute("details", t.getClass().getName());
				}
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			return writer.toString();
		} catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	protected Map<String, Object> createJson( GHResponse rsp, boolean calcPoints,
			boolean pointsEncoded, boolean includeElevation, boolean enableInstructions )
			{
		Map<String, Object> json = new HashMap<String, Object>();

		if (rsp.hasErrors())
		{
			Map<String, String> map = new HashMap<String, String>();
			json.put("error", map);
			Throwable throwable = rsp.getErrors().get(0);
			map.put("message", throwable.getMessage());
			String statusCode = "" + HttpStatus.BAD_REQUEST_400;
			if (throwable instanceof APIException)
			{
				statusCode = "" + ((APIException) throwable).getStatusCode().getCode();
				logger.error("Unhandled exception, defaulting it to 400");
			}
			map.put("statuscode", statusCode);
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			for (Throwable t : rsp.getErrors())
			{
				Map<String, String> hintMap = new HashMap<String, String>();
				hintMap.put("message", t.getMessage());
				if (internalErrorsAllowed)
				{
					hintMap.put("details", t.getClass().getName());
				}
				list.add(hintMap);
			}
			json.put("hints", list);
		} else
		{
			Map<String, Object> jsonInfo = new HashMap<String, Object>();
			json.put("info", jsonInfo);
			// jsonInfo.put("copyrights", Arrays.asList("GraphHopper",
			// "OpenStreetMap contributors"));
			Map<String, Object> jsonPath = new HashMap<String, Object>();
			jsonPath.put("distance", Helper.round(rsp.getDistance(), 3));
			jsonPath.put("weight", Helper.round6(rsp.getDistance()));
			jsonPath.put("time", rsp.getTime());

			if (calcPoints)
			{
				jsonPath.put("points_encoded", pointsEncoded);

				PointList points = rsp.getPoints();
				if (points.getSize() >= 2)
				{
					BBox maxBounds = hopper.getGraph().getBounds();
					BBox maxBounds2D = new BBox(maxBounds.minLon, maxBounds.maxLon,
							maxBounds.minLat, maxBounds.maxLat);
					jsonPath.put("bbox", rsp.calcRouteBBox(maxBounds2D).toGeoJson());
				}

				jsonPath.put("points", createPoints(points, pointsEncoded, includeElevation));

				if (enableInstructions)
				{
					InstructionList instructions = rsp.getInstructions();
					jsonPath.put("instructions", instructions.createJson());
				}
			}
			json.put("paths", Collections.singletonList(jsonPath));
		}
		return json;
			}

	protected Object createPoints( PointList points, boolean pointsEncoded, boolean includeElevation )
	{
		if (pointsEncoded)
			return WebHelper.encodePolyline(points, includeElevation);

		Map<String, Object> jsonPoints = new HashMap<String, Object>();
		jsonPoints.put("type", "LineString");
		jsonPoints.put("coordinates", points.toGeoJson(includeElevation));
		return jsonPoints;
	}

	protected List<GHPoint> getPoints( HttpServletRequest req, String key )
	        throws InvalidParameterException
	{
		String[] pointsAsStr = getParams(req, key);
		final List<GHPoint> infoPoints = new ArrayList<GHPoint>(pointsAsStr.length);
		for (String str : pointsAsStr)
		{
			GHPoint point = GHPoint.parse(str);
			if (point != null)
			{
				infoPoints.add(point);
			} else
			{
				throw new InvalidParameterException(
				        "Point "
				                + str
				                + " is not a valid point. Point must be a comma separated coordinate in WGS84 projection.");
			}
		}

		return infoPoints;
	}

	protected void initHints( GHRequest request, Map<String, String[]> parameterMap )
	{
		WeightingMap m = request.getHints();
		for (Entry<String, String[]> e : parameterMap.entrySet())
		{
			if (e.getValue().length == 1)
				m.put(e.getKey(), e.getValue()[0]);
		}
	}
}
