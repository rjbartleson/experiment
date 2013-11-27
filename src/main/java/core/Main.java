package core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Main {

	private static final TimeZone TZ_UTC = TimeZone.getTimeZone("UTC");
	private static final SimpleDateFormat DT_FMTR = new SimpleDateFormat(
			"yyyy-MM-dd");
	private static final SimpleDateFormat DT_HH_FMTR = new SimpleDateFormat(
			"yyyy-MM-dd:HH");
	private static final SimpleDateFormat DT_FMTR_STD = new SimpleDateFormat(
			"yyyy-MM-dd:HH:mm:ss.SSSZ");
	static {
		DT_FMTR.setTimeZone(TZ_UTC);
		DT_HH_FMTR.setTimeZone(TZ_UTC);
		DT_FMTR_STD.setTimeZone(TZ_UTC);
	}

	public static final String ST_PILOT = "pilot";
	public static final String ST_ON_BOARDING = "on-boarding";
	public static final String ST_SUSTAINING = "sustaining";

	public static void main(String[] args) throws JSONException {
		// processJSONExample();
		// processDateToCounterMapping();

		//System.out.println(buildRegExForDateHHs("2012-11-15:14", "2012-11-16:22"));
		//System.out.println(buildRegExForDateRange("2012-11-16", "2013-04-13", "(12|13|14|15)"));

		final Map<String, Counter> countsMap = new TreeMap<String, Counter>();
		countsMap.put("org one", new Counter(5));
		countsMap.put("org two", new Counter(7));
		countsMap.put("org three", new Counter(5));
		countsMap.put("org four", new Counter(4));
		countsMap.put("org five", new Counter(7));
		final TreeMap<Integer, Set<String>> retMap = new TreeMap<Integer, Set<String>>();
		for (String grpVal : countsMap.keySet()) {
			Counter cntr = countsMap.get(grpVal);
			Integer key = new Integer(cntr.cnt);
			Set<String> set = retMap.get(key);
			if (set == null) {
				set = new TreeSet<String>();
			}
			set.add(grpVal);
			retMap.put(key, set);
		}
		for (Integer key : retMap.keySet()) {
			System.out.println(key);
			Set<String> set = retMap.get(key);
			for (String org : set) {
				System.out.print("\t" + org);
			}
			System.out.println();
		}

		Set<String> expKeys = new TreeSet<String>();
		Calendar cal = new GregorianCalendar(TZ_UTC);
		cal.add(Calendar.HOUR_OF_DAY, -1);
		final String highDateHH = DT_HH_FMTR.format(cal.getTime());
		expKeys.add(highDateHH);
		for (int i = 0; i < 22; i++) {
			cal.add(Calendar.HOUR_OF_DAY, -1);
			expKeys.add(DT_HH_FMTR.format(cal.getTime()));
		}
		cal.add(Calendar.HOUR_OF_DAY, -1);
		final String lowDateHH = DT_HH_FMTR.format(cal.getTime());
		expKeys.add(lowDateHH);

		System.out.println("\nSize: " + expKeys.size());
		int j = 0;
		for (String key : expKeys) {
			System.out.println(++j + ": " + key);
		}
	}

	protected static void processDateToCounterMapping() {
		Calendar cal1 = new GregorianCalendar(TZ_UTC);
		System.out.println("0 hour:" + DT_FMTR_STD.format(cal1.getTime()));
		cal1.add(Calendar.HOUR_OF_DAY, -1);
		cal1.set(Calendar.MINUTE, 59);
		cal1.set(Calendar.SECOND, 59);
		cal1.set(Calendar.MILLISECOND, 999);
		System.out.println("last hour:" + DT_FMTR_STD.format(cal1.getTime()));
		cal1.add(Calendar.HOUR_OF_DAY, -23);
		cal1.set(Calendar.MINUTE, 0);
		cal1.set(Calendar.SECOND, 0);
		cal1.set(Calendar.MILLISECOND, 0);
		System.out.println("first hour:" + DT_FMTR_STD.format(cal1.getTime()));
		for (int i = 0; i < 24; i++) {
			System.out.println(i + ": " + DT_FMTR_STD.format(cal1.getTime()));
			cal1.add(Calendar.HOUR_OF_DAY, 1);
		}

		/*
		 * set up
		 */
		String unit = "weeks"; // or "weeks"
		int nmbrOfUnits = 35;
		int nmbrOfDays = nmbrOfUnits * (unit.equals("days") ? 1 : 7);
		Calendar currCal = new GregorianCalendar(TZ_UTC);
		currCal.add(Calendar.DAY_OF_MONTH, -1);
		String endDate = DT_FMTR.format(currCal.getTime());
		currCal.add(Calendar.DAY_OF_MONTH, -nmbrOfDays + 1);
		String startDate = DT_FMTR.format(currCal.getTime());

		/*
		 * test code
		 */
		List<Counter> bins = new ArrayList<Counter>();
		for (int i = 0; i < nmbrOfUnits; i++) {
			bins.add(new Counter(i));
		}

		Map<String, Counter> dateIntMap = new TreeMap<String, Counter>();
		int endYear = Integer.parseInt(endDate.substring(0, 4));
		int endMonth = Integer.parseInt(endDate.substring(5, 7)) - 1; // 0 - 11
		int endDay = Integer.parseInt(endDate.substring(8, 10));
		int year = Integer.parseInt(startDate.substring(0, 4));
		int month = Integer.parseInt(startDate.substring(5, 7)) - 1; // 0 - 11
		int day = Integer.parseInt(startDate.substring(8, 10));
		Calendar cal = new GregorianCalendar(TZ_UTC);
		cal.set(year, month, day);
		Calendar endCal = new GregorianCalendar(TZ_UTC);
		endCal.set(endYear, endMonth, endDay);
		int binIdx = 0;
		int cntDays = 0;
		do {
			cntDays++;
			String date = DT_FMTR.format(cal.getTime());
			dateIntMap.put(date, bins.get(binIdx));
			if (unit.equals("days")) {
				binIdx++;
			} else {
				if (cntDays % 7 == 0) {
					binIdx++;
				}
			}
			cal.add(Calendar.DAY_OF_MONTH, 1);
		} while (!cal.after(endCal));

		for (String dateKey : dateIntMap.keySet()) {
			System.out.println(dateKey + ": " + dateIntMap.get(dateKey));
		}

		JSONArray arr = new JSONArray();
		for (Counter cntr : bins) {
			arr.put(cntr.cnt);
		}
		System.out.println("\narr: " + arr);
	}

	protected static void processJSONExample() throws JSONException {
		final JSONObject army = new JSONObject();
		army.put("organization", "Army");
		army.put("status", ST_SUSTAINING);
		army.put("oldStatus", ST_ON_BOARDING);
		army.put("deeProv", "123456");
		army.put("bbProv", "7890");
		army.put("OpsFlag", "statusChange");

		final JSONObject af = new JSONObject();
		af.put("organization", "Air Force");
		af.put("status", ST_ON_BOARDING);
		af.put("deeProv", "23432");
		af.put("bbProv", "4563");
		af.put("OpsFlag", "edit");

		final JSONObject navy = new JSONObject();
		navy.put("organization", "Navy");
		navy.put("status", ST_SUSTAINING);
		navy.put("deeProv", "23245");
		navy.put("bbProv", "3256");
		navy.put("OpsFlag", "edit");

		final JSONObject cc = new JSONObject();
		cc.put("organization", "Coast Guard");
		cc.put("status", ST_PILOT);
		cc.put("deeProv", "23245");
		cc.put("bbProv", "3256");
		cc.put("OpsFlag", "create");

		final JSONArray arr = new JSONArray();
		arr.put(army);
		arr.put(af);
		arr.put(navy);
		arr.put(cc);

		final JSONObject obj = new JSONObject();
		obj.put("JTOdata", arr);

		System.out.println(obj.toString());

		JSONObject clone = new JSONObject(obj.toString());
		JSONArray data = clone.getJSONArray("JTOdata");
		for (int i = 0; i < data.length(); i++) {
			JSONObject org = data.getJSONObject(i);
			System.out.println(org.toString());
		}

		final JSONObject respObj1 = new JSONObject();
		respObj1.put("status", ST_ON_BOARDING);
		respObj1.put("organization", "Army");
		respObj1.put("dee", "231432");
		respObj1.put("bb", "23541");
		respObj1.put("operation", "update");
		respObj1.put("operStatus", "success");

		final JSONObject respObj2 = new JSONObject();
		respObj2.put("status", ST_ON_BOARDING);
		respObj2.put("organization", "Air Force");
		respObj2.put("dee", "10342");
		respObj2.put("bb", "4512");
		respObj2.put("operation", "update");
		respObj2.put("operStatus", "failed");
		respObj2.put("error", "Error message generated by exception.");

		JSONArray respArr = new JSONArray();
		respArr.put(respObj1);
		respArr.put(respObj2);

		System.out.println(respArr.toString(4));
	}
	/**
	 * No more than two days between low and high values!
	 * 
	 * @param low
	 *            as yyyy-MM-dd:HH value
	 * @param high
	 *            as yyyy-MM-dd:HH value
	 * @return
	 */
	protected static String buildRegExForDateHHs(String low, String high) {
		final String lowDt = low.substring(0, 10);
		final String lowHH = low.substring(11);
		final String highDt = high.substring(0, 10);
		final String highHH = high.substring(11);

		String regex = "";
		if (lowDt.equals(highDt)) {
			if (lowHH.equals("00") && highHH.equals("23")) {
				regex = lowDt + ":\\d\\d";
			} else {
				regex = lowDt + ":" + createGroupLowToHigh(lowHH, highHH);
			}
		} else {
			String lowRegex = lowDt + ":" + createGroupLowToHigh(lowHH, "23");
			String highRegex = highDt + ":"
					+ createGroupLowToHigh("00", highHH);
			regex = lowRegex + "|" + highRegex;
		}
		return regex;
	}

	protected static String buildRegExForDateRange(String lowDate,
			String highDate, String hourGrp) {
		Calendar low = getCalendarForDate(lowDate);
		Calendar high = getCalendarForDate(highDate);
		Calendar workLow = getCalendarForDate(lowDate);
		Calendar workHigh = getCalendarForDate(lowDate);

		StringBuilder sb = new StringBuilder();
		workLow.set(Calendar.DAY_OF_MONTH, 1);
		workHigh.add(Calendar.MONTH, 1);
		workHigh.set(Calendar.DAY_OF_MONTH, 1);
		workHigh.add(Calendar.DAY_OF_MONTH, -1);
		do {
			System.out.println("Low:"+DT_FMTR.format(workLow.getTime()));
			System.out.println("High:"+DT_FMTR.format(workHigh.getTime()));
			// yyyy-MM-
			String yrMM = DT_FMTR.format(workLow.getTime()).substring(0, 8);
			String dd = "";
			if (!workLow.before(low) && !workHigh.after(high)) {
				dd = "\\d\\d";
			} else if (workLow.before(low) && !workHigh.after(high)) {
				dd = createGroupLowToHigh("" + low.get(Calendar.DAY_OF_MONTH),
						"" + workHigh.get(Calendar.DAY_OF_MONTH));
			} else {
				dd = createGroupLowToHigh("01",
						"" + high.get(Calendar.DAY_OF_MONTH));
			}

			if (sb.length() != 0) {
				sb.append("|");
			}
			sb.append(yrMM + dd);
			if (hourGrp != null) {
				sb.append(":" + hourGrp);
			}

			// set workLow to 1st of next month
			workLow.add(Calendar.MONTH, 1);
			workLow.set(Calendar.DAY_OF_MONTH, 1);
			// set workHigh to end of next month
			workHigh.setTime(workLow.getTime());
			workHigh.add(Calendar.MONTH, 1);
			workHigh.set(Calendar.DAY_OF_MONTH, 1);
			workHigh.add(Calendar.DAY_OF_MONTH, -1);
		} while (!workLow.after(high));

		return sb.toString();
	}

	/**
	 * Creates regular expression group compose of two-digit values ORed
	 * together from low to high values. Example for "03" to "12" is:
	 * "(03|04|05|06|07|08|09|10|11|12)".
	 * 
	 * @param low
	 * @param high
	 * @return
	 */
	private static String createGroupLowToHigh(String low, String high) {
		int start = Integer.parseInt(low);
		int end = Integer.parseInt(high);
		StringBuilder sb = new StringBuilder("(" + low);
		for (int i = start + 1; i <= end; i++) {
			sb.append("|");
			if (i < 10) {
				sb.append("0" + i);
			} else {
				sb.append(i);
			}
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Obtains a Calendar, with UTC time zone, set to midnight for given date.
	 * 
	 * @param date
	 *            yyyy-MM-dd format
	 * @return
	 */
	private static Calendar getCalendarForDate(String date) {
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1; // 0 - 11
		int day = Integer.parseInt(date.substring(8));
		final Calendar cal = new GregorianCalendar(TZ_UTC);
		cal.clear();
		cal.set(year, month, day, 0, 0, 0);
		return cal;
	}
}
