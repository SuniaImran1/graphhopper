Feature: Verify a route from A to B
   As a user
   I want to get a valid Error message and status code for a invalid route request

  #Error Messages
  #Successful request
  @Routing @ErrorMessages
  Scenario: Successful request with all parameters
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "car"
    And I have avoidances as ""
    And I have weighting as "fastest"
    And I have locale as "en"
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
      | wayPointIndex | waypointco                | waypointdesc                | azimuth | direction | time | distance | avoidance |
      | 2             | 50.729205,-3.523206 | Turn right onto WELL STREET | 210.0   | SW        | 4050 | 112.5    |           |

  # Parameter :  vehicle
  @Routing @ErrorMessages @Current
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
      | 123         |            | fastest   | Vehicle 123 is not a valid vehicle. Valid vehicles are car.   | 400        | Bad Request                |
      | foot        |            | fastest   | Vehicle foot is not a valid vehicle. Valid vehicles are car.  | 400        | Bad Request                 |
      | cycle       |            | fastest   | Vehicle cycle is not a valid vehicle. Valid vehicles are car. | 400        | Bad Request                 |
      | Bike        |            | fastest   | Vehicle Bike is not a valid vehicle. Valid vehicles are car.  | 400        | Bad Request                 |

  # Parameter :  vehicle
  @Routing @ErrorMessages
  Scenario Outline: Incorrect Parameter Name "vehicles"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicles as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | responseFormat | errorMessage | statusCode |
      | car         |            | fastest   | json           |              | 400        |

  # Parameter :  vehicle
  @Routing @ErrorMessages
  Scenario Outline: Missing Parameter "vehicle"
    Given I have route points as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | responseFormat | errorMessage                | statusCode |
      | car         |            | fastest   | json           | No point parameter provided | 400        |

  # Parameter :  point
  @Routing @ErrorMessages
  Scenario Outline: Incorrect Parameter Value "point"
    Given I have route point as
      | pointA           | pointB              |
      | 50.729961,string | 50.723364,-3.523895 |
    And I have vehicles as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         |            | fastest   | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  point
  @Routing @ErrorMessages
  Scenario Outline: Incorrect Parameter Name "points"
    Given I have route points as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicles as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | responseFormat | errorMessage                                                                                                       | statusCode |
      | car         |            | fastest   | json           | Parameter points is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  point
  @Routing @ErrorMessages
  Scenario Outline: Missing Parameter "point"
    Given I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | responseFormat | errorMessage                | statusCode |
      | car         |            | fastest   | json           | No point parameter provided | 400        |

  # Parameter :  avoidances
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Value for "avoidances"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | fastest   | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  avoidances
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Name for "avoidances"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidance as "<avoidances>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | fastest   | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  weighting
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Value for "weighting"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | faster    | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  weighting
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Name for "weighting"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidance as "<avoidances>"
    And I have weightings as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | fastest   | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  locale
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Value for "locale"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have locale as "<locale>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | locale | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | faster    | en     | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  locale
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Name for "locale"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidance as "<avoidances>"
    And I have locals as "<locale>"
    And I have weightings as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | locale | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | fastest   | en     | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  instructions
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Value for "instructions"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have instructions as "<instructions>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | instructions | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | faster    | msg("box")   | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  instructions
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Name for "instructions"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidance as "<avoidances>"
    And I have instruction as "<instructions>"
    And I have weightings as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | instructions | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | fastest   | true         | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  algorithm
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Value for "algorithm"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have algorithm as "<algorithm>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | algorithm | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | faster    | xyz       | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |
      | car         | trees      | faster    | dijkstra  | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |
      | car         | trees      | faster    | astar     | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |
      | car         | trees      | faster    | astarbi   | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  algorithm
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Name for "algorithm"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidance as "<avoidances>"
    And I have algorithms as "<algorithm>"
    And I have weightings as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | algorithm | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | fastest   | dijkstra  | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  points_encoded
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Value for "points_encoded"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have points_encoded as "<points_encoded>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | points_encoded | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | faster    | xyz            | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  points_encoded
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Name for "points_encoded"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidance as "<avoidances>"
    And I have points_encodedSSS as "<algorithm>"
    And I have weightings as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | points_encoded | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | fastest   | true           | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  debug
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Value for "debug"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have debug as "<debug>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | debug | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | faster    | xyz   | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  debug
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Name for "debug"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidance as "<avoidances>"
    And I have debug as "<debug>"
    And I have weightings as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | debug | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | fastest   | true  | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  calc_points
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Value for "calc_points"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have calc_points as "<debug>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | calc_points | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | faster    | xyz         | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  calc_points
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Name for "calc_points"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidance as "<avoidances>"
    And I have calc_points as "<calc_points>"
    And I have weightings as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | calc_points | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | fastest   | true        | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  Type
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Value for "type"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have calc_points as "<debug>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | calc_points | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | faster    | xyz         | txt            | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  # Parameter :  Type
  @Routing @ErrorMessages
  Scenario Outline: Invalid Parameter Name for "calc_points"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    And I have vehicle as "<vehicleType>"
    And I have avoidance as "<avoidances>"
    And I have calc_pointSSS as "<calc_points>"
    And I have weightings as "<routeType>"
    And I have responseType as "<responseFormat>"
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | calc_points | responseFormat | errorMessage                                                                                                              | statusCode |
      | car         | trees      | fastest   | true        | json           | Parameter calc_pointSSS is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 400        |

  @Routing @ErrorMessages
  Scenario Outline: Invalid http method "PUT"
    Given I have route point as
      | pointA              | pointB              |
      | 50.729961,-3.524853 | 50.723364,-3.523895 |
    Given I have vehicle as "<vehicleType>"
    And I have avoidances as "<avoidances>"
    And I have weighting as "<routeType>"
    And I have type as "<responseFormat>"
    And I request for HTTP "PUT" method
    When I request for a route
    Then I should be able to verify the response message as "<errorMessage>"
    Then I should be able to verify the statuscode as "<statusCode>"

    Examples: 
      | vehicleType | avoidances | routeType | responseFormat | errorMessage                                                                                                     | statusCode |
      | car         | trees      | fastest   | json           | Parameter blah is not a valid parameter for resource nearest. Valid parameters for requested resource are point. | 405        |