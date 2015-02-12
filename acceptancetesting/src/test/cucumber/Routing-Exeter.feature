#  Exeter Only Routing Scenarios
Feature: Verify a route from A to B
    As a user
    I want to get a route from location A to location B using the routing service
    And route should be the fastest route and contain the waypoints,restrictions,time and other instructions

  # One Way Restrictions
  @Routing
  Scenario Outline: Verify  one Way  Restrictions  on a Route (EX-Bridge South - Exteter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                      | azimuth | direction | time  | distance |
      | 2             | 50.719156,-3.537811 | Continue onto A3015 (FROG STREET) | 54      | NE        | 14517 | 221.824  |

    Examples: 
      | pointA              | pointB              | routetype |
      | 50.717615,-3.536538 | 50.719106,-3.535359 | car       |

  # Same route but different waypointdesc
  @Routing
  Scenario Outline: Verify  one Way  Restrictions on a Route (Cleveladn Street-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco         | waypointdesc                     | azimuth | direction | time | distance |
      | 2             | 50.717806,-3.54264 | Turn sharp left onto BULLER ROAD | 124     | SE        | 5744 | 55.845   |

    Examples: 
      | pointA              | pointB              | routetype |
      | 50.717951,-3.542331 | 50.718613,-3.539589 | car       |

  @Routing
  Scenario Outline: Verify  one Way  Restrictions on a Route (Cleveladn Street-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                    | azimuth | direction | time  | distance |
      | 4             | 50.718462,-3.541302 | Turn left onto CLEVELAND STREET | 244     | SW        | 12258 | 119.183  |

    Examples: 
      | pointA              | pointB              | routetype |
      | 50.718282,-3.538437 | 50.717687,-3.541511 | car       |

  @Routing
  Scenario Outline: Verify  one Way  Restrictions (Except Buses) on a Route (SIDWELL STREET-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco         | waypointdesc                    | azimuth | direction | time | distance |
      | 4             | 50.726689,-3.52712 | Turn left onto LONGBROOK STREET | 196     | S         | 8057 | 78.339   |

    Examples: 
      | pointA              | pointB               | routetype |
      | 50.727949,-3.523498 | 50.726428,-3.5251291 | car       |

  @Routing
  Scenario Outline: Verify  oneway Restrictions on a Route (Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the trackPoints not on the route map:
      | trackPointco       |
      | 50.71958,-3.534089 |

    Examples: 
      | pointA              | pointB             | routetype |
      | 50.720492,-3.535221 | 50.718641,-3.53476 | car       |

  @Routing
  Scenario Outline: Verify  one Way  Restrictions  on a Route (Exeter WSPIP-83)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints not on the route map:
      | wayPointIndex | waypointco          | waypointdesc                    | azimuth | direction | time | distance |
      | 7             | 50.722198,-3.526704 | Turn left onto SOUTHERNHAY EAST | 32      | NE        | 5838 | 56.761   |

    Examples: 
      | pointA              | pointB              | routetype |
      | 50.720454,-3.530089 | 50.722657,-3.526321 | car       |

  # No Entry Restrictions
  @Routing
  Scenario Outline: Verify  No Entry  Restrictions on a Route (High Street(London Inn Square)-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco         | waypointdesc                                 | azimuth | direction | time  | distance |
      | 2             | 50.725549,-3.52693 | Turn slight left onto NEW NORTH ROAD (B3183) | 279     | W         | 75355 | 732.658  |

    Examples: 
      | pointA              | pointB             | routetype |
      | 50.725425,-3.526925 | 50.72442,-3.532756 | car       |

  @Routing
  Scenario Outline: Verify  No Entry  Restrictions on a Route (CHEEK STREET-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                           | azimuth | direction | time | distance |
      | 2             | 50.727244,-3.522476 | Turn sharp left onto SUMMERLAND STREET | 302     | NW        | 6716 | 65.31    |

    Examples: 
      | pointA              | pointB             | routetype |
      | 50.726234,-3.524072 | 50.727186,-3.52392 | car       |

  @Routing
  Scenario Outline: Verify  No Entry(Except for Buses and Taxis)  Restrictions on a Route (Sidwell Street-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco         | waypointdesc                    | azimuth | direction | time  | distance |
      | 4             | 50.726418,-3.52381 | Turn left onto BAMPFYLDE STREET | 58      | NE        | 13514 | 131.399  |

    Examples: 
      | pointA              | pointB             | routetype |
      | 50.726529,-3.524928 | 50.727002,-3.52419 | car       |

  # No Turns Restrictions
  @Routing
  Scenario Outline: Verify  No Turn  Restrictions on a Route (Western Way-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                | azimuth | direction | time | distance |
      | 2             | 50.728509,-3.520647 | Turn slight left onto B3212 | 282     | W         | 2241 | 21.797   |

    Examples: 
      | pointA              | pointB              | routetype |
      | 50.726735,-3.520955 | 50.726914,-3.522033 | car       |

  @Routing
  Scenario Outline: Verify  No Turn Restriction (Denmark Road-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                  | azimuth | direction | time  | distance |
      | 3             | 50.725002,-3.520632 | Turn left onto RUSSELL STREET | 293     | NW        | 25597 | 248.881  |

    Examples: 
      | pointA              | pointB              | routetype |
      | 50.724901,-3.521588 | 50.724524,-3.520923 | car       |

  @Routing
  Scenario Outline: Verify  Turn Restrictions  on a Route (Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the trackPoints not on the route map:
      | trackPointco        |
      | 50.721201,-3.532498 |

    Examples: 
      | pointA             | pointB             | routetype |
      | 50.72148,-3.532485 | 50.721888,-3.53182 | car       |

  @Routing
  Scenario Outline: Verify No  Turn Restrictions(Except Bus)  on a Route (BELGROVE ROAD -Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints not on the route map:
      | wayPointIndex | waypointco         | waypointdesc                       | azimuth | direction | time | distance |
      | 2             | 50.725997,-3.52296 | Turn sharp left onto CHEEKE STREET | 135     | SE        | 5639 | 56.915   |

    Examples: 
      | pointA              | pointB             | routetype |
      | 50.726085,-3.522837 | 50.725076,-3.52442 | car       |

  # Mandatory Turn Restrictions
  @Routing
  Scenario Outline: Verify  Mandatory Turn(with exceptions) at Exeter area
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                | azimuth | direction | time | distance |
      | 2             | 50.726462,-3.523882 | Continue onto CHEEKE STREET | 121     | SE        | 725  | 7.054    |

    Examples: 
      | pointA              | pointB              | routetype |
      | 50.726823,-3.524432 | 50.725423,-3.526813 | car       |

  @Routing
  Scenario Outline: Verify  Mandatory Turn at Exeter area(DENMARK ROAD)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                  | azimuth | direction | time  | distance |
      | 2             | 50.725002,-3.520632 | Turn left onto RUSSELL STREET | 293     | NW        | 25597 | 248.881  |

    Examples: 
      | pointA              | pointB              | routetype |
      | 50.724777,-3.520811 | 50.724394,-3.520953 | car       |

  Scenario Outline: Verify  Mandatory Turn at Exeter area(COLLEGE ROAD)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco         | waypointdesc                | azimuth | direction | time  | distance |
      | 2             | 50.72133,-3.519451 | Turn right onto SPICER ROAD | 278     | W         | 41233 | 400.903  |

    Examples: 
      | pointA             | pointB              | routetype |
      | 50.723597,-3.51776 | 50.723773,-3.517251 | car       |

  @Routing
  Scenario Outline: Verify  Mandatory Turn Restriction (Denmark Road-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints not on the route map:
      | wayPointIndex | waypointco          | waypointdesc                           | azimuth | direction | time  | distance |
      | 2             | 50.724703,-3.520835 | Turn right onto HEAVITREE ROAD (B3183) | 105     | E         | 15636 | 152.027  |

    Examples: 
      | pointA              | pointB             | routetype |
      | 50.724378,-3.520993 | 50.72413,-3.518874 | car       |

  # Access Limited To
  @Routing
  Scenario Outline: Verify  Access Limited To  Restrictions on a Route (North Street-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco       | waypointdesc               | azimuth | direction | time  | distance |
      | 2             | 50.72258,-3.5326 | Continue onto SOUTH STREET | 123     | SE        | 38986 | 379.059  |

    Examples: 
      | pointA              | pointB              | routetype |
      | 50.722996,-3.533354 | 50.726428,-3.525129 | car       |

  @Routing
  Scenario Outline: Verify  Access Limited To  Restrictions on a Route (Paris Street-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco         | waypointdesc                    | azimuth | direction | time  | distance |
      | 5             | 50.726418,-3.52381 | Turn left onto BAMPFYLDE STREET | 58      | NE        | 13514 | 131.399  |

    Examples: 
      | pointA              | pointB              | routetype |
      | 50.724989,-3.526006 | 50.729735,-3.519862 | car       |

  # Access Prohibited To
  @Routing
  Scenario Outline: Verify  Access Prohibited To  Restrictions on a Route (Iron Bridge Street-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco         | waypointdesc                         | azimuth | direction | time  | distance |
      | 2             | 50.724661,-3.53639 | Turn sharp left onto ST DAVID'S HILL | 299     | NW        | 45198 | 439.449  |

    Examples: 
      | pointA             | pointB              | routetype |
      | 50.72458,-3.536493 | 50.723442,-3.534131 | car       |

  @Routing
  Scenario Outline: Verify  Access Prohibited To  Restrictions on a Route (Upper Paul Street-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                      | azimuth | direction | time  | distance |
      | 2             | 50.724819,-3.532223 | Turn sharp left onto QUEEN STREET | 312     | NW        | 37994 | 369.415  |

    Examples: 
      | pointA              | pointB              | routetype |
      | 50.724614,-3.532555 | 50.724639,-3.530457 | car       |

  # Ford
  @Routing
  Scenario Outline: Verify  Ford  Restrictions on a Route (BONHAY Road-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints not on the route map:
      | wayPointIndex | waypointco          | waypointdesc                              | azimuth | direction | time  | distance |
      | 3             | 50.730325,-3.541923 | Turn slight right onto A377 (BONHAY ROAD) | 217     | SW        | 87530 | 1337.351 |

    Examples: 
      | pointA             | pointB              | routetype |
      | 50.731111,-3.54277 | 50.719327,-3.538255 | car       |

  @Routing
  Scenario Outline: Verify  Ford  Restrictions on a Route (Quadrangle Road-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc               | azimuth | direction | time  | distance |
      | 2             | 50.730716,-3.530028 | Turn left onto HORSEGUARDS | 194     | S         | 28148 | 273.666  |

    Examples: 
      | pointA             | pointB              | routetype |
      | 50.730861,-3.52934 | 50.731808,-3.529829 | car       |

  # Gate
  @Routing
  Scenario Outline: Verify  Gate  Restrictions on a Route (Cathedral Close Road-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                           | azimuth | direction | time  | distance |
      | 3             | 50.722198,-3.526704 | Turn sharp right onto SOUTHERNHAY EAST | 214     | SW        | 26318 | 255.907  |

    Examples: 
      | pointA              | pointB             | routetype |
      | 50.722333,-3.527488 | 50.72243,-3.532372 | car       |

  @Routing
  Scenario Outline: Verify  Gate  Restrictions on a Route (Lower Northen Road-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc        | azimuth | direction | time  | distance |
      | 2             | 50.722081,-3.539012 | Turn left onto A377 | 160     | S         | 25882 | 395.48   |

    Examples: 
      | pointA            | pointB              | routetype |
      | 50.7244,-3.535817 | 50.723705,-3.534493 | car       |

  #Private Road
  Scenario Outline: Verify  a Private Road (Publicly Accessible) on a Route  (PERRY ROAD)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco         | waypointdesc                    | azimuth | direction | time  | distance |
      | 2             | 50.732011,-3.53798 | Turn right onto STREATHAM DRIVE | 3       | N         | 17156 | 166.807  |

    Examples: 
      | pointA              | pointB              | routetype |
      | 50.732296,-3.535372 | 50.733538,-3.537462 | car       |

  Scenario Outline: Verify a  Private Road (Publicly Accessible) on a Route (QUEEN STREET)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco          | waypointdesc                                 | azimuth | direction | time  | distance |
      | 2             | 50.727397,-3.535531 | Turn slight left onto NEW NORTH ROAD (B3183) | 287     | W         | 18644 | 181.293  |

    Examples: 
      | pointA              | pointB              | routetype |
      | 50.727003,-3.535041 | 50.727023,-3.533083 | car       |

  Scenario Outline: Verify a PrivateRoad -Restricted Access(WESTERN WAY)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the waypoints on the route map:
      | wayPointIndex | waypointco         | waypointdesc                      | azimuth | direction | time | distance |
      | 1             | 50.72593,-3.521909 | Continue onto B3212 (WESTERN WAY) | 51      | NE        | 3985 | 38.745   |

    Examples: 
      | pointA              | pointB             | routetype |
      | 50.725876,-3.521801 | 50.72619,-3.521541 | car       |

  Scenario Outline: Verify a Private Road - Restricted Access (Denmark Road-Exeter)
    Given I request a route between "<pointA>" and "<pointB>" as a "<routetype>" from RoutingAPI
    Then I should be able to verify the trackPoints not on the route map:
      | trackPointco      |
      | 50.723966,-3.5198 |

    Examples: 
      | pointA              | pointB             | routetype |
      | 50.724316,-3.521008 | 50.72413,-3.518874 | car       |
