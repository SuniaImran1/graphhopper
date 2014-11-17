#LOGIT=
LOGIT=:${HOME}/.m2/repository/org/slf4j/slf4j-log4j12/1.7.7/slf4j-log4j12-1.7.7.jar:${HOME}/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar

java -Xmx4096m -Xms2048m -XX:+UseParallelGC -XX:+UseParallelOldGC -cp ../tools/target/classes:target/classes:${HOME}/.m2/repository/uk/co/ordnancesurvey/api/OSGBCoordinateConvertor/1.0.3/OSGBCoordinateConvertor-1.0.3.jar:${HOME}/.m2/repository/org/slf4j/slf4j-api/1.7.7/slf4j-api-1.7.7.jar:${HOME}/.m2/repository/net/sf/trove4j/trove4j/3.0.3/trove4j-3.0.3.jar${LOGIT} com.graphhopper.tools.OsITNProblemRouteExtractor osmreader.osm=./ITNun/ reader.implementation=com.graphhopper.reader.osgb.OsItnReader roadName="CHURCH ROAD" linkRoadName="DAWLISH ROAD"
