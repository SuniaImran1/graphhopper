Feature: Smoke Tests :Verify a route from A to B
  
   As a user
   I want to get a route from location A to location B using the routing service
   And route should be the fastest route and contain the waypoints,restrictions,time and other instructions

  @Smoke
  Scenario: Successful request with all parameters
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "car"
    And I have avoidances as ""
    And I have weighting as "fastest"
    And I have locale as "en_US"
    And I have debug as "true"
    And I have points_encoded as "true"
    And I have points_calc as "true"
    And I have instructions as "true"
    And I have algorithm as "astar"
    And I have type as "json"
    When I request for a route
    Then I should be able to verify the http response message as "OK"
    Then I should be able to verify the http statuscode as "200"
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                | azimuth | direction | time | distance | avoidance |
      | 2             | 50.729205,-3.523206 | Turn right onto WELL STREET | 210.0   | SW        | 4050 | 112.5    |           |

  @Smoke
  Scenario Outline: Incorrect Parameter Value for "Vehicle"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    When I request for a route
    Then I should be able to verify the http response message as "<httpErrorMessage>"
    Then I should be able to verify the http statuscode as "<statusCode>"
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | errorMessage                                                  | statusCode | httpErrorMessage |
      | 123         |            | fastest   | Vehicle 123 is not a valid vehicle. Valid vehicles are car.   | 400        | Bad Request      |
      | foot        |            | fastest   | Vehicle foot is not a valid vehicle. Valid vehicles are car.  | 400        | Bad Request      |
      | cycle       |            | fastest   | Vehicle cycle is not a valid vehicle. Valid vehicles are car. | 400        | Bad Request      |
      | Bike        |            | fastest   | Vehicle Bike is not a valid vehicle. Valid vehicles are car.  | 400        | Bad Request      |

  @Smoke
  Scenario Outline: Verify  waypoints on a Route from Southampton to Glasgow
    Given I have route point as
      | pointA              | pointB             |
      | 50.896617,-1.400465 | 55.861284,-4.24996 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    When I request for a route
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                                             | azimuth | direction | time    | distance | avoidance |
      | 1             | 50.896796,-1.400544 | Continue onto PLATFORM ROAD (A33)                        | 254.0   | W         | 3192    | 84.3     |           |
      | 16            | 50.951921,-1.404239 | At roundabout, take exit 1 onto A33                      | 318.0   | NW        | 7083    | 187.0    |           |
      | 17            | 50.953446,-1.403571 | Turn slight right onto M3                                | 28.0    | NE        | 566900  | 15747.6  |           |
      | 18            | 51.07086,-1.292917  | At roundabout, take exit 2 onto A34 (WINCHESTER BY-PASS) | 284.0   | W         | 55129   | 1454.8   |           |
      | 20            | 51.868385,-1.199845 | At roundabout, take exit 1 onto M40                      | 357.0   | N         | 2636747 | 73242.2  |           |
      | 24            | 52.381175,-1.790061 | At roundabout, take exit 1 onto A34 (STRATFORD ROAD)     | 301.0   | NW        | 46514   | 1227.5   |           |

    Examples: 
      | vehicleType | avoidances | routeType |
      | car         |            | fastest   |

  @Smoke
  Scenario Outline: Verify  waypoints on a Route from London to Birmingham and the total route time estimate
    Given I have route point as
      | pointA              | pointB              |
      | 51.507229,-0.127581 | 52.481875,-1.898743 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    When I request for a route
    Then The total route time should be not more than "<totalRouteTime>"

    Examples: 
      | vehicleType | avoidances | routeType | totalRouteTime |
      | car         |            | fastest   | 03h00min       |

  @Smoke @ServiceOnly
  Scenario Outline: Verify  waypoints on a Route from Southampton to Glasgow
    Given I have route point as
      | pointA              | pointB             |
      | 50.896617,-1.400465 | 55.861284,-4.24996 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    When I request for a route
    Then I should be able to verify the trackPoints on the route map:
      | trackPointco        |
      | 52.52355,-1.902136  |
      | 53.779418,-2.647821 |
      | 54.304996,-2.646641 |
      | 55.802602,-4.053713 |

    Examples: 
      | vehicleType | avoidances | routeType |
      | car         |            | fastest   |

  @Smoke
  Scenario Outline: Verify  No Turn   (WSPIP-76:Eastley- TWYFORD ROAD )
    Given I have route point as
      | pointA              | pointB              |
      | 50.972281,-1.350942 | 50.972212,-1.351183 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    When I request for a route
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                | azimuth | direction | time | distance | avoidance |
      | 3             | 50.971952,-1.350891 | Turn left onto THE CRESCENT | 294.0   | NW        | 2981 | 37.3     |           |

    Examples: 
      | vehicleType | avoidances | routeType |
      | car         |            | fastest   |

  @Smoke
  Scenario Outline: Verify  No Turn   (WSPIP-76:Eastley- Station Hill Road)
    Given I have route point as
      | pointA              | pointB             |
      | 50.970024,-1.350267 | 50.97008,-1.350521 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    When I request for a route
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                      | azimuth | direction | time | distance | avoidance |
      | 2             | 50.969817,-1.350504 | Continue onto STATION HILL (A335) | 180.0   | S         | 4583 | 57.3     |           |

    Examples: 
      | vehicleType | avoidances | routeType |
      | car         |            | fastest   |

  @Smoke
  Scenario Outline: Verify  Mandatory Turn   (Alexandra Road-Hounslow- Fairfields Road)
    Given I have route point as
      | pointA             | pointB              |
      | 51.47118,-0.363609 | 51.470254,-0.363412 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    When I request for a route
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                   | azimuth | direction | time | distance | avoidance |
      | 2             | 51.470846,-0.363527 | Turn right onto LANSDOWNE ROAD | 259.0   | W         | 9934 | 124.2    |           |

    Examples: 
      | vehicleType | avoidances | routeType |
      | car         |            | fastest   |

  @WebOnly
  Scenario Outline: Verify  Route using Full UK Address (Southampton to London)
    Given I have route point as
      | pointA                                                            | pointB                                 |
      | ORDNANCE SURVEY, 4, ADANAC DRIVE, NURSLING, SOUTHAMPTON, SO16 0AS | 1, PICCADILLY ARCADE, LONDON, SW1Y 6NH |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    When I request for a route
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointdesc                                  |
      | 3             | At roundabout, take exit 2 onto BROWNHILL WAY |
      | 18            | Continue onto PICCADILLY (A4)                 |

    Examples: 
      | vehicleType | avoidances | routeType |
      | car         |            | fastest   |

  @WebOnly
  Scenario Outline: Verify  Route using Full UK Address (Hounslow to Slough)
    Given I have route point as
      | pointA                              | pointB                                      |
      | 131, TIVOLI ROAD, HOUNSLOW, TW4 6AS | 40, CHILTERN ROAD, BURNHAM, SLOUGH, SL1 7NH |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    When I request for a route
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointdesc                                   |
      | 9             | At roundabout, take exit 1 onto BATH ROAD (A4) |
      | 10            | Turn right onto HUNTERCOMBE LANE NORTH         |

    Examples: 
      | vehicleType | avoidances | routeType |
      | car         |            | fastest   |

  @Smoke
  Scenario Outline: Verify a Roundabout(Charles Watts Way)
    Given I have route point as
      | pointA             | pointB              |
      | 50.915416,-1.31902 | 50.915551,-1.294049 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    When I request for a route
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                                             | azimuth | direction | time  | distance | avoidance |
      | 3             | 50.920147,-1.310351 | At roundabout, take exit 2 onto CHARLES WATTS WAY (A334) | 0.0     | N         | 17647 | 465.7    |           |

    Examples: 
      | vehicleType | avoidances | routeType |
      | car         |            | fastest   |

  @Smoke
  Scenario Outline: Verify  nearest point of point using NearestPoint API
    Given I have type as "<responseFormat>"
    And My routing point for nearestPoint API as "<pointA>"
    When I request a nearest point from from Nearest Point API
    Then I should be able to verify the nearest point to be "<pointB>" at a distance of "<distance>"

    Examples: 
      | pointA                                 | pointB                                 | distance           |
      | 51.878966,-0.903849                    | 51.875144098888576,-0.9107481891829116 | 636.3215777261629  |
      | 53.101756,-1.954888                    | 53.10043020792586,-1.961414745138117   | 460.0011625834564  |
      | 53.065293927002806,-1.9071498141906338 | 53.065293927002806,-1.9071498141906338 | 0                  |
      | 52.784893,-1.84522                     | 52.79515894789604,-1.8521510478589918  | 1233.001210212637  |
      | 52.79515894789604,-1.8521510478589918  | 52.79515894789604,-1.8521510478589918  | 0                  |
      | 54.094977,-2.006081                    | 54.09420551570219,-2.0283477834242833  | 1454.551799711362  |
      | 54.115309,-2.111881                    | 54.133065323525635,-2.131028334744908  | 2335.612435123903  |
      | 54.095897,-2.144795                    | 54.08689388826998,-2.1488754559056935  | 1035.8644445463867 |
      | 50.658849,-1.386463                    | 50.65520130477257,-1.4000444889283343  | 1039.7773305822475 |
      | 56.025277,-4.917874                    | 56.02196904113215,-4.906092518708935   | 819.3253424080308  |
      | 50.664175,-1.358463                    | 50.66192580003871,-1.3486298102579224  | 736.8284619868352  |

  @Smoke
  Scenario Outline: Verify  Route using 2 intermediate waypoints (Route-120 :Perth to Edinburgh via Stirling and Glasgow )
    Given I have route point as
      | pointA             | pointB              | pointC              | pointD              |
      | 56.38721,-3.466273 | 56.136656,-3.970408 | 55.871665,-4.195067 | 55.950467,-3.208924 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    When I request for a route
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                | azimuth | direction | time   | distance | avoidance |
      | 5             | 56.204647,-3.952177 | Turn slight left onto B8033 | 225.0   | SW        | 42144  | 585.4    |           |
      | 22            | 55.871622,-4.198356 | Turn slight right onto M8   | 43.0    | NE        | 278576 | 7738.5   |           |

    Examples: 
      | vehicleType | avoidances | routeType |
      | car         |            | shortest  |

  @Smoke
  Scenario Outline: Verify  Route using 2 intermediate waypoints (Route-120 :Perth to Edinburgh via Stirling and Glasgow )
    Given I have route point as
      | pointA             | pointB              | pointC              | pointD              |
      | 56.38721,-3.466273 | 56.136656,-3.970408 | 55.871665,-4.195067 | 55.950467,-3.208924 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    When I request for a route
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                       | azimuth | direction | time   | distance | avoidance |
      | 5             | 56.170837,-3.970499 | At roundabout, take exit 3 onto M9 | 91.0    | E         | 142970 | 3961.4   |           |
      | 15            | 55.871772,-4.195164 | Continue onto M8                   | 243.0   | SW        | 10568  | 293.6    |           |

    Examples: 
      | vehicleType | avoidances | routeType |
      | car         |            | fastest   |
