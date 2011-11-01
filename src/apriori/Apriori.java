/**
 * Data Mining 
 * CSE 601
 * HomeWork 2
 * 
 * Team Members:
 * Kunal Phapunkar 	(kunalpha@buffalo.edu)
 * Rishi Baldawa	(rishibal@buffalo.edu)
 * Vaibhav Porwal	(vaibhavp@buffalo.edu)
 * 
 * 
 * NOTE that program is non-modular single java file as per project requirements. 
 */

package apriori;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * 
 * @author rishi baldawa
 * 
 *         TASK : Develop an implementation of Apriori Algorithm that can work
 *         on any data set in any format or at least require minimum parsing, to
 *         calculate frequent item sets and important association rules.
 * 
 *         TAGS: Apriori, Algorithm, Knowledge, Discovery, Association, Rules,
 *         Frequent, Item, Set, Data, Mining
 * 
 */
public class Apriori {
	static Map<String, Integer> SampleFrequency = new HashMap<String, Integer>();
	static Map<String, Integer> AssociateRules = new HashMap<String, Integer>();
	static Map<String, Integer> StrongAssociateRules = new HashMap<String, Integer>();
	static SortedSet<String> AssociateRulesSet = new TreeSet<String>();

	/**
	 * Main function. Requires minimum parsing.
	 * 
	 * @param args
	 *            unused
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		System.out.println("Enter fileName");
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);

		String fileName = reader.readLine();
		System.out.println("Enter confidence");
		float confidence = Float.valueOf(reader.readLine().trim()).floatValue();

		System.out.println("Enter support");
		float support = Float.valueOf(reader.readLine().trim()).floatValue();

		int totalSamples = 0;
		BufferedReader inSample = new BufferedReader(new FileReader(fileName));
		while (inSample.readLine() != null)
			totalSamples++;
		int samplesCounter = -1;
		float supportItems = (support * totalSamples);
		Map<String, Integer> Frequency = new HashMap<String, Integer>();

		System.out.println("Start");

		System.out.println("TOTAL SAMPLES : " + totalSamples);
		System.out.println("SUPPORT : " + support);
		System.out.println("CONFIDENCE : " + confidence);
		BufferedReader inList = new BufferedReader(new FileReader(fileName));
		String str;// Temp String Variable for input buffered reader
		while ((str = inList.readLine()) != null) {
			samplesCounter++;// for labelling
			StringTokenizer stToken = new StringTokenizer(str, "\t");

			/* First one is the sample name. Ignored */
			stToken.nextToken();
			int tokenCounter = -1;// for labelling
			while (stToken.hasMoreTokens()) {
				String token = stToken.nextToken();
				tokenCounter++;

				if (token.toLowerCase().contains("up")) {
					addMap(Frequency, "G" + Integer.toString(tokenCounter)
							+ "up");
					SampleFrequency.put(Integer.toString(samplesCounter) + "@G"
							+ Integer.toString(tokenCounter) + "up",
							samplesCounter);
				} else if (token.toLowerCase().contains("down")) {
					addMap(Frequency, "G" + Integer.toString(tokenCounter)
							+ "down");
					SampleFrequency.put(Integer.toString(samplesCounter) + "@G"
							+ Integer.toString(tokenCounter) + "down",
							samplesCounter);
				} else {
					addMap(Frequency, token);
					SampleFrequency.put(Integer.toString(samplesCounter) + "@"
							+ token, samplesCounter);
				}
			}

		}
		inList.close();

		System.out.println("******************");
		System.out.println("All Frequent Item Sets");
		System.out.println("******************");

		SortedSet<String> set1 = FrequencyWithSupport(Frequency, supportItems);
		AssociateRulesSet.addAll(set1);

		Iterator<String> it = set1.iterator();
		int counter = 0;
		while (it.hasNext()) {
			String s = (String) it.next();
			System.out.println(s);
			counter++;
		}

		System.out.println("**********************");

		int i = 2;
		while (!set1.isEmpty()) {

			System.out.println("LEVEL : " + i);
			Map<String, Integer> map1 = NextCandidates(set1, i, supportItems);

			set1.clear();
			set1 = FrequencyWithSupport(map1, supportItems);
			it = set1.iterator();
			while (it.hasNext()) {
				String s = (String) it.next();
				System.out.println(s);
				Frequency.put(s, map1.get(s));
			}

			AssociateRulesSet.addAll(set1);
			i++;
		}

		it = AssociateRulesSet.iterator();
		counter = 0;
		while (it.hasNext()) {
			System.out.println(it.next().toString());
			counter++;
		}
		System.out.println("------------------");
		System.out.println("NO OF FREQ ITEM SETS : " + counter);

		/*
		 * Frequent Item Set Found. Starting Association Rules from here
		 */

		SortedSet<String> AssociateRuleCandidatesSet = new TreeSet<String>();
		it = AssociateRulesSet.iterator();
		while (it.hasNext()) {
			AssociateRulesFinder(it.next().toString(),
					AssociateRuleCandidatesSet);
		}

		/*
		 * All Associate Rules found. not finding Strong Associate Rules
		 */

		System.out.println("******************");
		System.out.println("Strong Associate Rules");
		System.out.println("******************");
		it = AssociateRuleCandidatesSet.iterator();
		counter = 0;
		while (it.hasNext()) {
			String tempItString = it.next().toString();

			StringTokenizer st1 = new StringTokenizer(tempItString, "@");
			String FreqItemSetString = st1.nextToken().toString();
			String AssociateRuleLHS = st1.nextToken().toString();
			StringTokenizer st2 = new StringTokenizer(AssociateRuleLHS, "->");
			AssociateRuleLHS = st2.nextToken().toString();
			String AssociateRuleRHS = st2.nextToken().toString();

			// Time to find Frequencies
			int FreqItemSetStringCount = Frequency.get(FreqItemSetString);
			int AsssociateRuleLHSCount = Frequency.get(AssociateRuleLHS);

			if (FreqItemSetStringCount >= confidence * AsssociateRuleLHSCount
					&& FreqItemSetStringCount >= supportItems) {
				System.out.println("{" + AssociateRuleLHS + "}->{"
						+ AssociateRuleRHS + "}");
				StrongAssociateRules.put("{" + AssociateRuleLHS + "}->{"
						+ AssociateRuleRHS + "}", AsssociateRuleLHSCount
						/ FreqItemSetStringCount);

			}
			counter++;
		}
		System.out.println("------------------");
		System.out.println("NO OF STRONG ASSOCIATION RULES : " + counter);

		System.out.println("******************");
		System.out.println("Templates For Associate Rules");
		System.out.println("******************");

		while (true) {
			System.out.println("Type a rule and press just 'Enter' to Exit");
			String string = "";
			input = new InputStreamReader(System.in);
			reader = new BufferedReader(input);

			try {
				string = reader.readLine();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (string.contentEquals("") || string.contentEquals("\r")
					|| string.contentEquals("\n"))
				break;// exiting
			else
				System.out.println("You typed: " + string);

			/* Processing rule */
			it = TemplatesAssociationRule(string).iterator();
			while (it.hasNext()) {
				System.out.println(it.next());
			}
		}

		System.out.println("Fin");
	}/* END OF MAIN */

	/**
	 * @param string
	 *            contains association rule condition
	 * @return parses (just like WHERE clause in SQL) the condition and gives
	 *         out rules that satisfy the condition
	 */
	private static Set<String> TemplatesAssociationRule(String string) {

		SortedSet<String> returnSet = new TreeSet<String>();

		if (string.contains("AND")) {

			Set<String> return1 = TemplatesAssociationRule(string.substring(0,
					string.indexOf(" AND ")));
			Set<String> return2 = TemplatesAssociationRule(string
					.substring(string.indexOf(" AND ") + 5));

			/* AND of two rules */
			Iterator<String> it = return1.iterator();
			while (it.hasNext()) {
				String returnStringRuleVal = it.next().toString();
				if (return2.contains(returnStringRuleVal)) {
					returnSet.add(returnStringRuleVal);
				}
			}

		} else if (string.contains("OR")) {

			Set<String> return1 = TemplatesAssociationRule(string.substring(0,
					string.indexOf(" OR ")));
			Set<String> return2 = TemplatesAssociationRule(string
					.substring(string.indexOf(" OR ") + 4));

			/* OR of two rules */
			returnSet.addAll(return1);
			returnSet.addAll(return2);

		} else if (string.contains("SizeOf")) {

			StringTokenizer st1 = new StringTokenizer(string);
			st1.nextToken();
			st1.nextToken();
			int ruleBodyInt = Integer.parseInt(st1.nextToken().toString());

			/* Processing Command */
			if (string.contains("SizeOf(BODY)")) {
				Iterator<String> it = StrongAssociateRules.keySet().iterator();
				while (it.hasNext()) {
					String ruleString = it.next().toString();
					StringTokenizer st = new StringTokenizer(ruleString, "->");
					String RHS = st.nextToken().toString();

					// st=new StringTokenizer(RH)
					RHS = RHS.substring(1, RHS.length() - 1);
					st = new StringTokenizer(RHS, ",");
					int bodyTokenSize = st.countTokens();

					if (string.contains(">=")) {

						if (bodyTokenSize >= ruleBodyInt)
							returnSet.add(ruleString);

					} else if (string.contains("<=")) {

						if (bodyTokenSize <= ruleBodyInt)
							returnSet.add(ruleString);
					} else if (string.contains("==")) {

						if (bodyTokenSize == ruleBodyInt)
							returnSet.add(ruleString);
					} else if (string.contains("<>")) {

						if (bodyTokenSize != ruleBodyInt)
							returnSet.add(ruleString);
					} else if (string.contains("<")) {

						if (bodyTokenSize < ruleBodyInt)
							returnSet.add(ruleString);
					} else if (string.contains(">")) {

						if (bodyTokenSize > ruleBodyInt)
							returnSet.add(ruleString);
					}
				}

			} else if (string.contains("SizeOf(HEAD)")) {

				Iterator<String> it = StrongAssociateRules.keySet().iterator();
				while (it.hasNext()) {
					String ruleString = it.next().toString();
					StringTokenizer st = new StringTokenizer(ruleString, "->");
					String LHS = st.nextToken().toString();

					LHS = LHS.substring(1, LHS.length() - 1);
					st = new StringTokenizer(LHS, ",");
					int headTokenSize = st.countTokens();

					if (string.contains(">=")) {

						if (headTokenSize >= ruleBodyInt)
							returnSet.add(ruleString);

					} else if (string.contains("<=")) {

						if (headTokenSize <= ruleBodyInt)
							returnSet.add(ruleString);
					} else if (string.contains("==")) {

						if (headTokenSize == ruleBodyInt)
							returnSet.add(ruleString);
					} else if (string.contains("<>")) {

						if (headTokenSize != ruleBodyInt)
							returnSet.add(ruleString);
					} else if (string.contains("<")) {

						if (headTokenSize < ruleBodyInt)
							returnSet.add(ruleString);
					} else if (string.contains(">")) {

						if (headTokenSize > ruleBodyInt)
							returnSet.add(ruleString);
					}
				}

			} else if (string.contains("SizeOf(RULE)")) {
				Iterator<String> it = StrongAssociateRules.keySet().iterator();
				while (it.hasNext()) {
					String ruleString = it.next().toString();
					StringTokenizer st = new StringTokenizer(ruleString, "->");
					String LHS = st.nextToken().toString();
					String RHS = st.nextToken().toString();

					LHS = LHS.substring(1, LHS.length() - 1);
					RHS = RHS.substring(1, RHS.length() - 1);
					LHS = LHS + "," + RHS;
					st = new StringTokenizer(LHS, ",");
					int TokenSize = st.countTokens();

					if (string.contains(">=")) {

						if (TokenSize >= ruleBodyInt)
							returnSet.add(ruleString);

					} else if (string.contains("<=")) {

						if (TokenSize <= ruleBodyInt)
							returnSet.add(ruleString);
					} else if (string.contains("==")) {

						if (TokenSize == ruleBodyInt)
							returnSet.add(ruleString);
					} else if (string.contains("<>")) {

						if (TokenSize != ruleBodyInt)
							returnSet.add(ruleString);
					} else if (string.contains("<")) {

						if (TokenSize < ruleBodyInt)
							returnSet.add(ruleString);
					} else if (string.contains(">")) {

						if (TokenSize > ruleBodyInt)
							returnSet.add(ruleString);
					}
				}

			} else {
				System.err.println("##FORMAT ERROR##");
			}

		} else {
			/* Processing Command { } HAS { } OF ({},..) */
			String itemString = string.substring(string.lastIndexOf("(") + 1,
					string.lastIndexOf(")"));
			SortedSet<String> itemSet = new TreeSet<String>();
			StringTokenizer st = new StringTokenizer(itemString, ",");
			while (st.hasMoreElements()) {
				itemSet.add(st.nextToken());
			}

			/* Getting the NUMBER/ANY/ALL */
			int numberCondition = 0;
			st = new StringTokenizer(string);
			st.nextToken();
			st.nextToken();
			String tempString = st.nextToken();
			if (tempString.equals("ANY")) {
				numberCondition = -2;
			} else if (tempString.equals("ALL")) {
				numberCondition = -4;
			} else {
				numberCondition = Integer.parseInt(tempString);
			}
			Iterator<String> it = StrongAssociateRules.keySet().iterator();
			while (it.hasNext()) {

				if (string.contains("HEAD")) {

					String ruleString = it.next().toString();
					StringTokenizer st1 = new StringTokenizer(ruleString, "->");
					String LHS = st1.nextToken().toString();
					LHS = LHS.substring(1, LHS.length() - 1);

					SortedSet<String> itemSetLHS = new TreeSet<String>();
					st1 = new StringTokenizer(LHS, ",");
					while (st1.hasMoreTokens()) {
						itemSetLHS.add(st1.nextToken());
					}

					if (numberCondition == -4) {
						if (itemSetLHS.containsAll(itemSet))
							returnSet.add(ruleString);

					} else if (numberCondition == -2) {
						Iterator<String> it2 = itemSet.iterator();
						while (it2.hasNext()) {
							if (LHS.contains(it2.next().toString())) {
								returnSet.add(ruleString);
								break;
							}
						}

					} else if (numberCondition > 0) {
						int matchCounter = 0;
						Iterator<String> it2 = itemSet.iterator();
						while (it2.hasNext()) {
							if (LHS.contains(it2.next().toString())) {
								matchCounter++;
							}
						}
						if (matchCounter == numberCondition) {
							returnSet.add(ruleString);
						}
					} else {
						System.err.println("##FORMAT ERROR##");
					}

				} else if (string.contains("BODY")) {

					String ruleString = it.next().toString();
					StringTokenizer st1 = new StringTokenizer(ruleString, "->");
					String RHS = st1.nextToken().toString();
					RHS = RHS.substring(1, RHS.length() - 1);

					SortedSet<String> itemSetRHS = new TreeSet<String>();
					st1 = new StringTokenizer(RHS, ",");
					while (st1.hasMoreTokens()) {
						itemSetRHS.add(st1.nextToken());
					}

					if (numberCondition == -4) {
						if (itemSetRHS.containsAll(itemSet))
							returnSet.add(ruleString);

					} else if (numberCondition == -2) {
						Iterator<String> it2 = itemSet.iterator();
						while (it2.hasNext()) {
							if (RHS.contains(it2.next().toString())) {
								returnSet.add(ruleString);
								break;
							}
						}

					} else if (numberCondition > 0) {
						int matchCounter = 0;
						Iterator<String> it2 = itemSet.iterator();
						while (it2.hasNext()) {
							if (RHS.contains(it2.next().toString())) {
								matchCounter++;
							}
						}
						if (matchCounter == numberCondition) {
							returnSet.add(ruleString);
						}
					} else {
						System.err.println("##FORMAT ERROR##");
					}

				} else if (string.contains("RULE")) {

					String ruleString = it.next().toString();
					StringTokenizer st1 = new StringTokenizer(ruleString, "->");
					String LHS = st1.nextToken().toString();
					String RHS = st1.nextToken().toString();
					LHS = LHS.substring(1, LHS.length() - 1);
					RHS = RHS.substring(1, RHS.length() - 1);
					LHS = LHS + "," + RHS;

					SortedSet<String> itemSetLHS = new TreeSet<String>();
					st1 = new StringTokenizer(LHS, ",");
					while (st1.hasMoreTokens()) {
						itemSetLHS.add(st1.nextToken());
					}

					if (numberCondition == -4) {
						if (itemSetLHS.containsAll(itemSet))
							returnSet.add(ruleString);

					} else if (numberCondition == -2) {
						Iterator<String> it2 = itemSet.iterator();
						while (it2.hasNext()) {
							if (LHS.contains(it2.next().toString())) {
								returnSet.add(ruleString);
								break;
							}
						}

					} else if (numberCondition > 0) {
						int matchCounter = 0;
						Iterator<String> it2 = itemSet.iterator();
						while (it2.hasNext()) {
							if (LHS.contains(it2.next().toString())) {
								matchCounter++;
							}
						}
						if (matchCounter == numberCondition) {
							returnSet.add(ruleString);
						}
					} else {
						System.err.println("##FORMAT ERROR##");
					}

				} else {
					System.err.println("##FORMAT ERROR##");
				}
			}
		}
		return returnSet;
	}/* END OF FUNCTION */

	/**
	 * 
	 * @param string
	 *            contains the rule
	 * @param associateRuleCandidatesSet
	 *            possible candidates that satisfy the rule
	 */
	private static void AssociateRulesFinder(String string,
			SortedSet<String> associateRuleCandidatesSet) {
		StringTokenizer AssociateRulesTokenizer = new StringTokenizer(string,
				",");
		int AssociateRulesTokenSize = AssociateRulesTokenizer.countTokens();
		if (AssociateRulesTokenSize == 1) {
			return;
		}

		Set<String> AssociateRuleCandidateTokens = new HashSet<String>();
		while (AssociateRulesTokenizer.hasMoreTokens()) {
			AssociateRuleCandidateTokens.add(AssociateRulesTokenizer
					.nextToken().toString());
		}

		String[] elements = AssociateRuleCandidateTokens.toArray(new String[0]);
		for (int j = 1; j < AssociateRulesTokenSize; j++) {
			int[] indices;
			CombinationGenerator x = new CombinationGenerator(elements.length,
					j);
			while (x.hasMore()) {
				SortedSet<String> tempFreqItemsElement = new TreeSet<String>();
				indices = x.getNext();
				for (int i = 0; i < indices.length; i++) {
					tempFreqItemsElement.add(elements[indices[i]].toString());
				}/* END OF FOR */

				if (tempFreqItemsElement.size() == j) {
					/* REMOING Duplicates */

					/* converting [1, 2] to 1,2 */
					String tempString = (tempFreqItemsElement.toString());
					tempString = removeSpaces(tempString);
					tempString = tempString.substring(1,
							tempString.length() - 1);

					String rhs = new String();
					Iterator<String> tokenTempIt = AssociateRuleCandidateTokens
							.iterator();
					while (tokenTempIt.hasNext()) {
						String tempTokenString = tokenTempIt.next().toString();

						if (!tempFreqItemsElement.contains(tempTokenString)) {
							rhs += (tempTokenString + ",");
						}
					}

					rhs = rhs.substring(0, rhs.length() - 1);
					associateRuleCandidatesSet.add(string + "@" + tempString
							+ "->" + rhs);
				}
			}
		}
		return;
	}/* END OF FUNCTION */

	/**
	 * 
	 * @param set1
	 * @param level
	 * @return
	 * 
	 *         Generate candidates for L(k) from L(k-1)
	 * @throws IOException
	 */
	private static Map<String, Integer> NextCandidates(SortedSet<String> set1,
			int level, float supportItems) throws IOException {
		int totalSamples = 100;

		Map<String, Integer> returnMap = new HashMap<String, Integer>();
		SortedSet<String> copy1 = new TreeSet<String>();

		Iterator<String> it = set1.iterator();
		while (it.hasNext()) {
			String setString = (String) it.next();
			StringTokenizer setST = new StringTokenizer(setString, ",");
			if (setST.countTokens() != level - 1) {
				System.out.println("ERROR count tokens : "
						+ setST.countTokens() + " Level : " + level);
				// System.exit(level);
				return returnMap;
			}
			while (setST.hasMoreTokens()) {
				String token = setST.nextToken();
				copy1.add(token);
			}
		}

		String[] elements = copy1.toArray(new String[0]);
		copy1.clear();

		if (level > elements.length)
			return returnMap;
		int[] indices;
		CombinationGenerator x = new CombinationGenerator(elements.length,
				level);
		while (x.hasMore()) {
			SortedSet<String> tempFreqItemsElement = new TreeSet<String>();
			indices = x.getNext();
			for (int i = 0; i < indices.length; i++) {
				tempFreqItemsElement.add(elements[indices[i]].toString());
			}/* END OF FOR */

			if (tempFreqItemsElement.size() == level) {

				/* converting [1, 2] to 1,2 */
				String tempString = (tempFreqItemsElement.toString());
				tempString = removeSpaces(tempString);
				tempString = tempString.substring(1, tempString.length() - 1);

				for (int i = 0; i < totalSamples; i++) {
					String sampleKey = Integer.toString(i);

					int matchCounter = 0;
					String setString = tempString;
					StringTokenizer setST = new StringTokenizer(setString, ",");
					if (setST.countTokens() != level) {
						System.out.println("ERROR count tokens");
						System.exit(level);
					}
					while (setST.hasMoreTokens()) {
						String token = setST.nextToken();

						if (SampleFrequency
								.containsKey(sampleKey + "@" + token))
							matchCounter++;
						else
							matchCounter = 0;
					}
					if (matchCounter == level) {
						addMap(returnMap, setString);
					}
				}
			}
		}/* END OF WHILE */

		return returnMap;
	}/* END OF FUNCTION */

	/**
	 * 
	 * @param s
	 * @return
	 * 
	 *         Removes Spaces
	 */
	private static String removeSpaces(String s) {
		StringTokenizer st = new StringTokenizer(s, " ", false);
		String t = "";
		while (st.hasMoreElements())
			t += st.nextElement();
		return t;
	}/* END OF FUCTION */

	/**
	 * 
	 * @param frequency2
	 * @param supportItems
	 * @return
	 * 
	 *         Check the count for each candidate and return sorted set that has
	 *         count greater the min_sup*total_samples
	 */
	private static SortedSet<String> FrequencyWithSupport(
			Map<String, Integer> frequency2, float supportItems) {

		SortedSet<String> returnSortedSet = new TreeSet<String>();

		Iterator<String> it = frequency2.keySet().iterator();
		while (it.hasNext()) {
			String str = (String) it.next();
			int freqVal = frequency2.get(str);
			if (freqVal >= supportItems)
				returnSortedSet.add(str);
		}

		return returnSortedSet;
	}/* END OF FUNCTION */

	/**
	 * 
	 * @param m
	 * @param Item
	 * 
	 *            adds to the count of the map
	 */
	private static void addMap(Map<String, Integer> m, String Item) {
		if (m.containsKey(Item)) {
			int i = m.get(Item);
			m.put(Item, ++i);
		} else {
			m.put(Item, 1);
		}

	}/* END OF FUNCTION */

}/* END OF CLASS */
