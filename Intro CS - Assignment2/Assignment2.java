import java.lang.reflect.Array;

public class Assignment2 {

	/*-----------------------
	 *| Part A - tasks 1-11 |
	 * ----------------------*/

	// task 1
	public static boolean isSquareMatrix(boolean[][] matrix) {
		boolean isSquare = true;
		// for no exceptions :
		if (matrix == null) {
			return false;
		}
		if (matrix.length == 0) {
			return false;
		}
		// check if isSquare
		for (int i = 0; i < matrix.length & isSquare; i = i + 1) {
			if (matrix[i] == null || matrix[i].length == 0) {
				return false; // for no exceptions
			} else {
				if (matrix.length != matrix[i].length) {
					isSquare = false; // stop the loop, return false
				}
			}
		}
		return isSquare;
	}

	// task 2
	public static boolean isSymmetricMatrix(boolean[][] matrix) {
		boolean isSymmetric = true;
		// check if is Symmetric
		for (int i = 0; i < matrix.length & isSymmetric; i = i + 1) {
			for (int j = 0; j < matrix.length & isSymmetric; j = j + 1) {
				if (matrix[i][j] != matrix[j][i])
					isSymmetric = false; // stop the loop, return false
			}
		}
		return isSymmetric;
	}

	// task 3
	public static boolean isAntiReflexiveMatrix(boolean[][] matrix) {
		boolean isAntiReflexive = true;
		// check if is AntiReflexive
		for (int i = 0; i < matrix.length & isAntiReflexive; i = i + 1) {
			if (matrix[i][i] != false)
				isAntiReflexive = false; // stop the loop, return false
		}
		return isAntiReflexive;
	}

	// task 4
	public static boolean isLegalInstance(boolean[][] matrix) {
		boolean isLegalInstance = true;
		// for no exceptions :
		if (matrix == null) {
			isLegalInstance = false;
		}
		if (matrix.length == 0) {
			isLegalInstance = false;
		}
		// use previous functions to check the values:
		boolean isSquare = isSquareMatrix(matrix);
		boolean isSymmetric = isSymmetricMatrix(matrix);
		boolean isAntiReflexive = isAntiReflexiveMatrix(matrix);
		// check if is LegalInstance :
		for (int i = 0; i < matrix.length & isLegalInstance; i = i + 1) {
			if (isSquare == true & isSymmetric == true & isAntiReflexive == true) // check all the conditions
				isLegalInstance = true;
			else
				isLegalInstance = false; // stop the loop, return false
		}
		return isLegalInstance;
	}

	// task 5
	public static boolean isPermutation(int[] array) {
		boolean isPermutation = true;
		// check if is Permutation :
		for (int i = 0; i < array.length & isPermutation; i = i + 1) {
			isPermutation = false;
			for (int j = 0; j < array.length; j = j + 1) {
				if (i == array[j]) { // check if every number exits once
					isPermutation = true; // else, stop the loop - isPermutation=false
				}
			}
		}
		return isPermutation;
	}

	// task 6
	public static boolean hasLegalSteps(boolean[][] flights, int[] tour) {
		boolean hasLegalSteps = true;
		// check if hasLegalSteps:
		for (int i = 0; i < tour.length - 1 & hasLegalSteps == true; i = i + 1) {
			int currCity = tour[i];
			int nextCity = tour[i + 1];
			if (flights[currCity][nextCity] == false)
				hasLegalSteps = false; // stop the loop, return false
		}
		int currCity = tour[tour.length - 1]; // now we are in city 'n-1'
		int stCity = tour[0]; // we want to return to city '0' (first city)
		if (flights[currCity][stCity] == false) {
			hasLegalSteps = false; // check the flight from 'n-1' to '0'
		}
		return hasLegalSteps;
	}

	// task 7
	public static boolean isSolution(boolean[][] flights, int[] tour) {
		boolean isSolution = true;
		if (tour.length != flights.length)
			throw new IllegalArgumentException("tour length is illegal");
		// check if first city is '0':
		if (tour[0] != 0)
			return false;
		// use the previous functions to check if isSolution:
		boolean hasLegalSteps = hasLegalSteps(flights, tour);
		boolean isPermutation = isPermutation(tour);
		if (isPermutation == false)
			isSolution = false;
		if (hasLegalSteps == false)
			isSolution = false;
		return isSolution;
	}

	// task 8
	public static boolean evaluate(int[][] cnf, boolean[] assign) {
		// check if the cnf is satisfied :
		boolean ans = true;
		for (int i = 0; i < cnf.length & ans; i = i + 1) {
			ans = ans & evaluateClause(cnf[i], assign); // if the value from evaluateClause=false stop the loop, ans is
														// false
		}
		return ans;
	}

	// check the value of the assigns :
	public static boolean evaluateLiteral(int literal, boolean[] assign) {
		boolean ans;
		if (literal > 0)
			ans = assign[literal]; // if literal is positive the assign as it is
		else
			ans = !assign[-literal]; // if literal is negative the assign is the opposite value
		return ans;
	}

	// check if the clauses are satisfied :
	public static boolean evaluateClause(int[] clause, boolean[] assign) {
		boolean ans = false;
		for (int i = 0; i < clause.length & !ans; i = i + 1) {
			ans = ans | evaluateLiteral(clause[i], assign); // if the value from evaluateLiteral=false stop the loop,
															// ans
															// is false
		}
		return ans;
	}

	// task 9
	public static int[][] atLeastOne(int[] lits) {
		int numOfLits = lits.length;
		int numOfClauses = 1; // one clause of all the literals
		int[][] cnf = new int[numOfClauses][numOfLits];
		int[] clause = new int[numOfLits]; // all the literals are in the clause, if at least one is true- true.
		// fill the cnf:
		for (int i = 0; i < lits.length; i = i + 1) {
			clause[i] = lits[i]; // put all the literals in the clause
			cnf[0] = clause;
		}
		return cnf;
	}

	// task 10
	public static int[][] atMostOne(int[] lits) {
		int numOfLits = lits.length;
		int numOfClauses = numOfLits * (numOfLits - 1) / 2; // every literal has (numOfLits - 1) different literals, /2
															// for duplicates
		int currIndex = 0;
		int[][] cnf = new int[numOfClauses][2];
		for (int i = 0; i < lits.length - 1; i = i + 1) {
			for (int j = i + 1; j < lits.length; j = j + 1, currIndex = currIndex + 1) {
				int[] clause = { -lits[i], -lits[j] }; // if both literals assigns are true- false
				cnf[currIndex] = clause;
			}
		}
		return cnf;
	}

	// task 11
	public static int[][] exactlyOne(int[] lits) {
		int numOfLits = lits.length;
		int numOfClauses = (numOfLits * (numOfLits - 1) / 2) + 1; // sum of the clauses of task9+10
		// use previous functions to create cnfs:
		int[][] atLeastOne = atLeastOne(lits);
		int[][] atMostOne = atMostOne(lits);
		// combine the previous cnfs to create the wanted cnf:
		int[][] cnf = new int[numOfClauses][];
		for (int currIndex = 0; currIndex < numOfClauses - 1; currIndex = currIndex + 1) {
			cnf[currIndex] = atMostOne[currIndex]; // fill cnf with clauses of atMostOne
		}
		cnf[numOfClauses - 1] = atLeastOne[0]; // last clause of the cnf is the clause of atLeastOne
		return cnf;
	}

	/*------------------------
	 *| Part B - tasks 12-20 |
	 * -----------------------*/

	// task 12a
	public static int map(int i, int j, int n) {
		int k = i * n + j + 1; // permanent ratio between k,i,j,n
		return k;
	}

	// task 12b
	public static int[] reverseMap(int k, int n) {
		int[] ans = new int[2];
		// as the ratio in task12a :
		int i = (k - 1) / n;
		int j = (k - 1) % n;
		ans[0] = i;
		ans[1] = j;
		return ans;
	}

	// task 13
	public static int[][] oneCityInEachStep(int n) {
		int numOfClause = n * ((n * (n - 1) / 2) + 1); // as in the exactlyOne cnf * n times (for each city)
		int[][] cnf = new int[numOfClause][];
		int[][] cnfOfOneCity = new int[numOfClause / n][]; // for one city
		int cnfCounter = 0;
		for (int i = 0; i < n; i = i + 1) {
			int[] nSteps = new int[n]; // array of the tour's steps
			int counter = 0;
			for (int j = 0; j < n; j = j + 1) {
				nSteps[counter] = map(i, j, n); // fill the array with values of the steps
				counter = counter + 1;
			}
			for (int cityCount = 0; cityCount < cnfOfOneCity.length; cityCount = cityCount + 1) {
				cnfOfOneCity[cityCount] = exactlyOne(nSteps)[cityCount]; // only one can be true in every step
			}
			for (int t = 0; t < cnfOfOneCity.length; t = t + 1) {
				cnf[cnfCounter] = cnfOfOneCity[t]; // fill the cnf with the created clauses of every city
				cnfCounter = cnfCounter + 1;
			}
		}
		return cnf;
	}

	// task 14
	public static int[][] eachCityIsVisitedOnce(int n) {
		int numOfClause = n * ((n * (n - 1) / 2) + 1); // as in oneCityInEachStep cnf
		int[][] cnf = new int[numOfClause][];
		int[][] cnfOfOneCity = new int[numOfClause / n][]; // for one city
		int cnfCounter = 0;
		for (int j = 0; j < n; j = j + 1) {
			int[] nSteps = new int[n]; // array of the tour's steps
			int counter = 0;
			for (int i = 0; i < n; i = i + 1) {
				nSteps[counter] = map(i, j, n); // fill the array with values of the steps
				counter = counter + 1;
			}
			for (int cityCounter = 0; cityCounter < cnfOfOneCity.length; cityCounter = cityCounter + 1) {
				cnfOfOneCity[cityCounter] = exactlyOne(nSteps)[cityCounter]; // only one can be true in every step
			}
			for (int t = 0; t < cnfOfOneCity.length; t = t + 1) {
				cnf[cnfCounter] = cnfOfOneCity[t]; // fill the cnf with the created clauses of every city
				cnfCounter = cnfCounter + 1;
			}
		}
		return cnf;
	}

	// task 15
	public static int[][] fixSourceCity(int n) {
		int[][] cnf = new int[1][1];
		cnf[0][0] = map(0, 0, n); // start level=0 in city '0'
		return cnf;
	}

	// task 16
	public static int[][] noIllegalSteps(boolean[][] flights) {
		int n = flights.length;
		// calculate how many illegal steps we have:
		int falseCounter = 0;
		for (int j = 0; j < n; j = j + 1) {
			for (int k = 0; k < n; k = k + 1) {
				if (flights[j][k] == false & j != k) // dont include city to same city
					falseCounter = falseCounter + 1;
			}
		}
		// create the cnf:
		int numOfClauses = falseCounter * n; // for each illegal step we loose n options to move
		int counter = 0;
		int[][] cnf = new int[numOfClauses][];
		if (numOfClauses != 0) { // else- cnf is empty
			for (int j = 0; j < n; j = j + 1) {
				for (int k = 0; k < n; k = k + 1) {
					if (k != j & flights[j][k] == false) { // if no flight from j to k, dont include city to same city
						for (int i = 0; i < n; i = i + 1) {
							int trueLit = map(i, j, n); // the step we did
							int falseLit = map((i + 1) % n, k, n); // the steps we cant do next
							int[] temp = { -trueLit, -falseLit };
							cnf[counter] = temp;
							counter = counter + 1;
						}
					}
				}
			}
		}
		return cnf;
	}

	// task 17
	public static int[][] encode(boolean[][] flights) {
		int n = flights.length;
		// create by previous functions 4 cnfs that assemble the wanted cnf:
		int[][] cnfOneEach = oneCityInEachStep(n);
		int[][] cnfVisitedOnce = eachCityIsVisitedOnce(n);
		int[][] cnfFixSource = fixSourceCity(n);
		int[][] cnfNoIllegal = noIllegalSteps(flights);
		// create the wanted cnf:
		int numOfClauses = cnfOneEach.length + cnfVisitedOnce.length + cnfFixSource.length + cnfNoIllegal.length; // sum
																													// the
																													// length
																													// of
																													// the
																													// cnfs
		int[][] cnf = new int[numOfClauses][];
		int counter = 0;
		// fill the wanted cnf with the clauses of the 4 cnfs
		for (int t = 0; t < cnfOneEach.length; t = t + 1) {
			cnf[counter] = cnfOneEach[t];
			counter = counter + 1;
		}
		for (int t = 0; t < cnfVisitedOnce.length; t = t + 1) {
			cnf[counter] = cnfVisitedOnce[t];
			counter = counter + 1;
		}
		for (int t = 0; t < cnfFixSource.length; t = t + 1) {
			cnf[counter] = cnfFixSource[t];
			counter = counter + 1;
		}
		for (int t = 0; t < cnfNoIllegal.length; t = t + 1) {
			cnf[counter] = cnfNoIllegal[t];
			counter = counter + 1;
		}
		return cnf;
	}

	// task 18
	public static int[] decode(boolean[] assignment, int n) {
		int[] tour = new int[n];
		if (assignment == null)
			throw new IllegalArgumentException("assignment is null");
		if (assignment.length != n * n + 1)
			throw new IllegalArgumentException("assignment length is illegal");
		// create an array of all the 'map' values:
		int[] maps = new int[n * n];
		for (int t = 0; t < n * n; t = t + 1) {
			maps[t] = t + 1;
		}
		// create tour:
		for (int m = 1; m < n * n + 1; m = m + 1) {
			if (assignment[m] == true) { // if its legal step add step to the tour
				int map = maps[m - 1]; // find the legal step value (map) in the maps array
				int i = reverseMap(map, n)[0]; // find the level
				int j = reverseMap(map, n)[1]; // find the city
				tour[i] = j;
			}
		}
		return tour;
	}

	// task19
	public static int[] solve(boolean[][] flights) {
		int n = flights.length;
		boolean isLegalInstance = isLegalInstance(flights);
		if (isLegalInstance == false)
			throw new IllegalArgumentException("illegal input");
		SATSolver.init(n * n); // initialize solver to the number of optional steps
		int[][] cnf = encode(flights);
		SATSolver.addClauses(cnf); // add the clauses of the cnf to Solver
		boolean[] assignment = SATSolver.getSolution(); // find satisfying assignment
		if (assignment.length != n * n + 1) // if no satisfying assignment return null
			return null;
		int[] tour = decode(assignment, n);
		boolean isSolution = isSolution(flights, tour); // check if the solution is legal
		if (isSolution == true)
			return tour;
		else
			throw new IllegalArgumentException("illegal solution");
	}

	// task20
	public static boolean solve2(boolean[][] flights) {
		boolean isLegalInstance = isLegalInstance(flights);
		if (isLegalInstance == false)
			throw new IllegalArgumentException("illegal input");
		int n = flights.length;
		int[] tour1 = solve(flights);
		boolean ans = true;
		int[] tour1a = new int[n]; // tour1 backwards
		// create tour1 backwards:
		for (int m = 1; m < n; m = m + 1) {
			tour1a[0] = 0; // first city is '0'
			tour1a[m] = tour1[n - m];
		}
		// create array of all the maps used in tour1 and tour1a:
		int[] mapsTour1 = new int[2 * (n - 1)];
		for (int i = 1, t = 0; i < n & t < n - 1; i = i + 1, t = t + 1) {
			mapsTour1[t] = map(i, tour1[i], n); // maps used in tour1
			mapsTour1[n - 1 + t] = map(i, tour1a[i], n); // maps used in tour1a
		}
		// check if there are same maps more than one time:
		for (int t = 0; t < mapsTour1.length - 1; t = t + 1) {
			for (int p = t + 1; p < mapsTour1.length; p = p + 1)
				if (mapsTour1[t] == mapsTour1[p] & mapsTour1[p] != 0)
					mapsTour1[p] = 0; // change value to neutral value
		}
		int[][] cnf = encode(flights); // general cnf
		// change the cnf - maps used in tour1 and tour1a are illegal:
		for (int t = 0; t < mapsTour1.length; t = t + 1) {
			for (int clause = 0; clause < cnf.length; clause = clause + 1) {
				for (int lit = 0; lit < cnf[clause].length; lit = lit + 1) {
					if (cnf[clause][lit] == mapsTour1[t])
						cnf[clause][lit] = -cnf[clause][lit]; // make it illegal
				}
			}
		}
		SATSolver.init(n * n); // initialize solver
		SATSolver.addClauses(cnf); // add the clauses of the new cnf to Solver
		boolean[] assignment = SATSolver.getSolution(); // find satisfying assignment
		int[] tour2 = decode(assignment, n); // create tour2
		boolean isSolution = isSolution(flights, tour2);
		if (isSolution == true)
			ans = true;
		else
			ans = false;
		return ans;
	}
}