/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatairportdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author srahman7
 */
public class FormatWunderData {

	/**
	 * @param date
	 * @return
	 */
	public static Date parseDate(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	private static long nextLong(Random rng, long n) {
		// TODO Auto-generated method stub
		long bits, val;
		do {
			bits = (rng.nextLong() << 1) >>> 1;
			val = bits % n;
		} while (bits - val + (n - 1) < 0L);
		return val;
	}

	public static String getOsName() {
		String OS = null;
		if (OS == null) {
			OS = System.getProperty("os.name");
		}
		return OS;
	}

	public static void createNT() throws ParseException {

		String csvFile;
		BufferedReader br = null;
		int date;
		Date myDate;
		Calendar cal = new GregorianCalendar();
		Map<String, String> carrierMap = new HashMap<String, String>();
		Map<String, AirPort> airportMap = new HashMap<String, AirPort>();
		String[] sample;
		String line = "";
		String schemafile;
		String datafile;
		String outputfile;
		String data = "flight";
		HashMap<String, Integer> ym = new HashMap<String, Integer>();

		try {

			int count = 0;
			if (getOsName().toLowerCase().contains("windows"))
				br = new BufferedReader(new FileReader("airports.csv"));
			else
				br = new BufferedReader(new FileReader("airports.csv"));

			while ((line = br.readLine()) != null) {
				if (count > 0) {
					sample = line.split(",");
					// System.out.println(line);
					if (sample[4].trim().equals("USA"))
						if (!sample[0].trim().matches(".*\\d+.*"))
							airportMap.put(sample[0].trim(), new AirPort(sample[1].trim(), sample[2].trim(),
									sample[3].trim(), sample[5].trim(), sample[6].trim()));
				}
				count++;
			}

			/*
			 * for(String key:airportMap.keySet()) { System.out.println(key); }
			 */

			br.close();

			long randomNum = 0, min = 1, max = Long.MAX_VALUE;
			Random rand = new Random();
			BufferedWriter bw = new BufferedWriter(new FileWriter("wunderrand.csv"));

			boolean leapYrCheck;
			int day_of_year;
			int totalCount = 0;
			String carrierD, weatherD, nasD, secD, aircraftD, taxOut, taxiIn;

			File folder = new File(System.getProperty("user.dir"));
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					csvFile = listOfFiles[i].getName();
					System.out.println("File " + csvFile);
					String [] key = listOfFiles[i].getName().split("-");
					if (airportMap.containsKey(key[0])) {

						leapYrCheck = true;
						File f = new File(csvFile);
						if (f.exists() && !f.isDirectory()) {
							line = "";

							br = new BufferedReader(new FileReader(csvFile));
							count = 0;

							while ((line = br.readLine()) != null) {

								// use comma as separator

								sample = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
								if (count > 0) {
									
									randomNum = nextLong(rand, max - min) + min;
									//System.out.println(key[3]);
									myDate = parseDate(key[3].split("\\.")[0].trim() + "-" + key[2].trim() + "-" + key[1].trim());

									cal.setTime(myDate);
									//System.out.println(cal.getTime());
									if (isLeapYear(Integer.parseInt(key[3].split("\\.")[0].trim())))
										day_of_year = cal.get(Calendar.DAY_OF_YEAR);
									else {
										if (Integer.parseInt(key[2].trim()) > 2)
											day_of_year = cal.get(Calendar.DAY_OF_YEAR) + 1;
										else
											day_of_year = cal.get(Calendar.DAY_OF_YEAR);
									}

									bw.write(convertTime(sample[0].trim()) + "," + day_of_year + ","
											+ cal.get(Calendar.MONTH) + "," + cal.get(Calendar.DAY_OF_MONTH) + ","
											+ cal.get(Calendar.DAY_OF_WEEK) + "," + cal.get(Calendar.WEEK_OF_YEAR) + ","
											+ cal.get(Calendar.YEAR) + "," + sample[1].trim() + "," + sample[2].trim()
											+ "," + sample[3].trim() + "," + sample[4].trim() + "," + sample[5].trim()
											+ "," + sample[7].trim() + "," + sample[11].trim() + ","
											+ airportMap.get(key[0]).city + "," + airportMap.get(key[0]).state + ","
											+ randomNum + "\n");
									totalCount++;
									

								}

								count++;

							}

							// System.out.println(count);
							br.close();
						}

					}
				} else if (listOfFiles[i].isDirectory()) {
					System.out.println("Directory " + listOfFiles[i].getName());
				}
			}

			// for(String key:ym.keySet())
			// System.out.println(key);
			System.out.println(totalCount);
			bw.close();
		} catch (Exception e) {
			System.out.println(line);
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static int convertTime(String time) {
		// TODO Auto-generated method stub
		String[] timeArr = time.split(":");
		//System.out.println(time);
		if (timeArr[0].contains("12") && timeArr[1].contains("AM")) {
			return 0;
		} else if (timeArr[0].contains("12") && timeArr[1].contains("PM")) {
			return 12;
		} else if (timeArr[1].contains("AM")) {
			return Integer.parseInt(timeArr[0].trim());
		} else if (timeArr[1].contains("PM")) {
			return Integer.parseInt(timeArr[0].trim()) + 12;
		}
		return 0;
	}

	private static boolean isLeapYear(int filename) {
		// TODO Auto-generated method stub
		if (filename % 400 == 0)
			return true;
		else if (filename % 4 == 0 && filename % 100 != 0)
			return true;

		return false;
	}

	private static void copyRandomized() {
		// TODO Auto-generated method stub
		try {
			BufferedReader br = null;
			BufferedWriter bw = new BufferedWriter(new FileWriter("weather.csv"));

			String line = "";
			String[] sample;

			int count = 0;

			bw.write("Hour of Day,Day,Month,Day Of Month,Day Of Week,Week Of Year,Year,"
					+ "Temperature (F),Dew Point (F),Humidity (%),Sea Level Pressure (in),Visibility (MPH),Wind Speed (MPH),Conditions,City,State\n");

			br = new BufferedReader(new FileReader("weather_.csv"));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				sample = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				if (!(sample[13].trim().equals("") || sample[14].trim().equals("") || sample[15].trim().equals("")
						|| sample[16].trim().equals("") || sample[17].trim().equals("") || sample[18].trim().equals("")
						|| sample[19].trim().equals("") || sample[20].trim().equals("")
						|| sample[21].trim().equals(""))) {
					bw.write(sample[0].trim() + "," + sample[1].trim() + "," + sample[2].trim() + "," + sample[3].trim()
							+ "," + sample[4].trim() + "," + sample[5].trim() + "," + sample[6].trim() + ","
							+ sample[7].trim() + "," + sample[8].trim() + "," + sample[9].trim() + ","
							+ sample[10].trim() + "," + sample[11].trim() + "," + sample[12].trim() + ","
							+ sample[13].trim() + "," + sample[14].trim() + "," + sample[15].trim() + ","
							+ sample[16].trim() + "," + sample[17].trim() + "," + sample[18].trim() + ","
							+ sample[19].trim() + "," + sample[20].trim() + "," + sample[21].trim() + "\n");
					count++;
				}

			}

			br.close();

			System.out.println(count);

			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

	public static void main(String[] args) {
		/*try { // TODO code application logic here createNT();
			createNT();
		} catch (Exception ex) {
			Logger.getLogger(FormatWunderData.class.getName()).log(Level.SEVERE, null, ex);
		}*/

		// calcAvg();

		// readFile();
		// copyRandomized();
		// withAllData();
		// countrows();
		mergeFiles();
	}

	private static void mergeFiles() {
		// TODO Auto-generated method stub
		try {
			BufferedReader br = null;
			// BufferedWriter bw = new BufferedWriter(new
			// FileWriter("flight2003.csv")); ;
			String line = "";
			String[] sample;
			BufferedWriter bw = new BufferedWriter(new FileWriter("weatherdata.csv"));
			bw.write("Airport,TimeEST,TemperatureF,Dew PointF,Humidity,Sea Level PressureIn,VisibilityMPH,Wind Direction,Wind SpeedMPH,Gust SpeedMPH,PrecipitationIn,Events,Conditions,WindDirDegrees,DateUTC\n");
			int totalcount = 0;
			File[] filesInDirectory = new File("./get_wunderground-master/").listFiles();
			for(File f : filesInDirectory){
				if(f.isFile())
				{
					String fileName = f.getName();
					System.out.println(fileName);
					br = new BufferedReader(new FileReader("./get_wunderground-master/"+fileName));
					int count = 0;
					while ((line = br.readLine()) != null) {

						if(count > 1)
						{
							bw.write(fileName.split("-")[0]+","+line+"\n"); 
							totalcount++;
						}
						count++;
					}

					br.close();
					
					/*if(true)
					{
						bw.close();
						return;
					}*/
					
				}
				
			}
			

			System.out.println(totalcount);

		 bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}
	
	private static void countrows() {
		// TODO Auto-generated method stub
		try {
			BufferedReader br = null;
			// BufferedWriter bw = new BufferedWriter(new
			// FileWriter("flight2003.csv")); ;
			String line = "";
			String[] sample;

			int count = 0;

			br = new BufferedReader(new FileReader("flight.csv"));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				count++;

			}

			br.close();

			System.out.println(count);

			// bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

	private static void withAllData() {
		// TODO Auto-generated method stub
		try {
			BufferedReader br = null;
			BufferedWriter bw = new BufferedWriter(new FileWriter("flight2003.csv"));
			;
			String line = "";
			String[] sample;

			int count = 0;

			br = new BufferedReader(new FileReader("flight.csv"));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				if (count == 0) {
					bw.write("Day,Month,Day Of Month,Day Of Week,Week Of Year,Year,Carrier,"
							+ "Origin Airport,Origin City,Origin State,Destination Airport,"
							+ "Destination City,Destination State,"
							+ "Departure Delay (minutes),Taxi Out (minutes),Taxi In (minutes),"
							+ "Arrival Delay (minutes),Carrier Delay (minutes),Weather Delay (minutes),"
							+ "NAS Delay (minutes),Security Delay (minutes),Aircraft Delay (minutes)\n");

				} else {
					sample = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
					if (Integer.parseInt(sample[5].trim()) == 2003 && Integer.parseInt(sample[1].trim()) >= 6) {
						bw.write(sample[0].trim() + "," + sample[1].trim() + "," + sample[2].trim() + ","
								+ sample[3].trim() + "," + sample[4].trim() + "," + sample[5].trim() + ","
								+ sample[6].trim() + "," + sample[7].trim() + "," + sample[8].trim() + ","
								+ sample[9].trim() + "," + sample[10].trim() + "," + sample[11].trim() + ","
								+ sample[12].trim() + "," + sample[13].trim() + "," + sample[14].trim() + ","
								+ sample[15].trim() + "," + sample[16].trim() + "," + sample[17].trim() + ","
								+ sample[18].trim() + "," + sample[19].trim() + "," + sample[20].trim() + ","
								+ sample[21].trim() + "\n");
						count++;

					} else if (Integer.parseInt(sample[5].trim()) > 2003) {
						bw.write(sample[0].trim() + "," + sample[1].trim() + "," + sample[2].trim() + ","
								+ sample[3].trim() + "," + sample[4].trim() + "," + sample[5].trim() + ","
								+ sample[6].trim() + "," + sample[7].trim() + "," + sample[8].trim() + ","
								+ sample[9].trim() + "," + sample[10].trim() + "," + sample[11].trim() + ","
								+ sample[12].trim() + "," + sample[13].trim() + "," + sample[14].trim() + ","
								+ sample[15].trim() + "," + sample[16].trim() + "," + sample[17].trim() + ","
								+ sample[18].trim() + "," + sample[19].trim() + "," + sample[20].trim() + ","
								+ sample[21].trim() + "\n");
						count++;

					}

				}

			}

			br.close();

			System.out.println(count);

			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

	private static void calcAvg() {
		String csvFile;
		BufferedReader br = null;
		int date;
		Date myDate;
		Calendar cal = new GregorianCalendar();
		double[] avg = new double[366];
		int[] counts = new int[366];
		try {
			BufferedWriter bw = new BufferedWriter(
					new FileWriter("/media/srahman7/OS/Users/srahman7/workspace/datarepo/airline_agg.csv"));

			csvFile = "/media/srahman7/OS/Users/srahman7/workspace/datarepo/airline.csv";

			// br = new BufferedReader(new FileReader(csvFile));
			String line = "";

			int index;
			double value;
			br = new BufferedReader(new FileReader(csvFile));
			int count = 0;
			// String[] sample;
			while ((line = br.readLine()) != null) {

				// use comma as separator

				// sample = line.split(",");
				if (count > 1) {
					// System.out.println(line);
					index = Integer.parseInt(line.split(",")[0].trim());
					value = Double.parseDouble(line.split(",")[4].trim());
					counts[index - 1]++;
					avg[index - 1] = ((avg[index - 1] * (counts[index - 1] - 1) + value) * 1.0) / counts[index - 1];
				}

				count++;

			}

			System.out.println(count);
			br.close();

			for (int i = 0; i < avg.length; i++) {
				bw.write((i + 1) + "," + avg[i] + "\n");

			}

			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/*
	 * // private static void randomize() { // String csvFile; // BufferedReader
	 * br = null; // int date; // Date myDate; // Calendar cal = new
	 * GregorianCalendar(); // double [] avg = new double[366]; // int [] counts
	 * = new int[366]; // try { // BufferedWriter bw = new BufferedWriter(new
	 * FileWriter(".\\result\\airline_r.csv")); // // // csvFile =
	 * ".\\result\\airline.csv"; // // //br = new BufferedReader(new
	 * FileReader(csvFile)); // String line = ""; // // // int day; // String
	 * carrier; // double arrival; // double departure; // br = new
	 * BufferedReader(new FileReader(csvFile)); // int count=1; // String[]
	 * sample; // ArrayList<AirData> ad = new ArrayList<AirData>(); // count =0;
	 * // while ((line = br.readLine()) != null) { //
	 * //System.out.println(++count); // // use comma as separator // //
	 * if(count == 5000000) // break; // sample = line.split(","); //
	 * day=Integer.parseInt(sample[0].trim()); // carrier = sample[1].trim(); //
	 * arrival = Double.parseDouble(sample[2].trim()); // departure =
	 * Double.parseDouble(sample[3].trim()); // // ad.add(new AirData(day,
	 * carrier, arrival, departure)); // count++; // // } // // // br.close();
	 * // // System.out.println(ad.size()); // long seed = System.nanoTime(); //
	 * Collections.shuffle(ad, new Random(seed)); // // for(int
	 * i=0;i<ad.size();i++) // { // if(i<5000) //
	 * System.out.println(ad.get(i).day+","+ad.get(i).carrier+","+ad.get(i).
	 * arrival+","+ad.get(i).departure); //
	 * bw.write(ad.get(i).day+","+ad.get(i).carrier+","+ad.get(i).arrival+","+ad
	 * .get(i).departure+"\n"); // } // //// for(int i=0;i<avg.length;i++) ////
	 * { //// bw.write((i+1)+","+avg[i]+"\n"); //// //// } // // // bw.close();
	 * // } catch (FileNotFoundException e) { // e.printStackTrace(); // } catch
	 * (IOException e) { // e.printStackTrace(); // } finally { // if (br !=
	 * null) { // try { // br.close(); // } catch (IOException e) { //
	 * e.printStackTrace(); // } // } // } // }
	 * 
	 * private static void randomize1() { String csvFile; BufferedReader br =
	 * null; int date; Date myDate; Calendar cal = new GregorianCalendar(); try
	 * { BufferedWriter bw = new BufferedWriter(new
	 * FileWriter(".\\result\\airline.csv"));
	 * 
	 * bw.write("Day,Month,DayOfMonth,Carrier,ArrDelay,DepDelay\n"); for(int
	 * filename = 1987;filename<=2008;filename++) { csvFile =
	 * ".\\data\\"+filename+".csv"; ArrayList<AirData> ad = new
	 * ArrayList<AirData>(); //br = new BufferedReader(new FileReader(csvFile));
	 * String line = "";
	 * 
	 * 
	 * 
	 * br = new BufferedReader(new FileReader(csvFile)); int count=0; //String[]
	 * sample; while ((line = br.readLine()) != null) {
	 * 
	 * // use comma as separator //sample = line.split(","); if(count > 0) {
	 * 
	 * if(!(line.split(",")[14].trim().contains("NA")||
	 * line.split(",")[15].trim().contains("NA"))) {
	 * 
	 * myDate =
	 * parseDate(line.split(",")[0].trim()+"-"+line.split(",")[1].trim()+"-"+
	 * line.split(",")[2].trim());
	 * 
	 * cal.setTime(myDate); //date =
	 * 365*(Integer.parseInt(line.split(",")[0].trim())-1987)+Integer.parseInt(
	 * line.split(",")[1].trim()); ad.add(new
	 * AirData(cal.get(Calendar.DAY_OF_YEAR),line.split(",")[1].trim(),line.
	 * split(",")[2].trim(),line.split(",")[8].trim(),line.split(",")[14].trim()
	 * ,line.split(",")[15].trim()));
	 * //bw.write(cal.get(Calendar.DAY_OF_YEAR)+","+line.split(",")[1].trim()+
	 * ","+line.split(",")[2].trim()+","+line.split(",")[8].trim()+","+line.
	 * split(",")[14].trim()+","+line.split(",")[15].trim()+"\n");
	 * 
	 * }
	 * 
	 * }
	 * 
	 * count++;
	 * 
	 * 
	 * }
	 * 
	 * long seed = System.nanoTime(); Collections.shuffle(ad, new Random(seed));
	 * System.out.println(ad.size()); for(int i=0;i<ad.size();i++) {
	 * 
	 * bw.write(ad.get(i).day+","+ad.get(i).month+","+ad.get(i).dayOfMonth+","+
	 * ad.get(i).carrier+","+ad.get(i).arrival+","+ad.get(i).departure+"\n"); }
	 * 
	 * 
	 * br.close(); }
	 * 
	 * bw.close(); } catch (FileNotFoundException e) { e.printStackTrace(); }
	 * catch (IOException e) { e.printStackTrace(); } finally { if (br != null)
	 * { try { br.close(); } catch (IOException e) { e.printStackTrace(); } } }
	 * }
	 * 
	 * private static void readFile() {
	 * 
	 * 
	 * String csvFile; BufferedReader br = null; int date; Date myDate; Calendar
	 * cal = new GregorianCalendar(); double [] avg = new double[366]; int []
	 * counts = new int[366]; try {
	 * 
	 * csvFile = ".\\result\\airline_rand.csv";
	 * 
	 * //br = new BufferedReader(new FileReader(csvFile)); String line = "";
	 * 
	 * 
	 * int index; double value; br = new BufferedReader(new
	 * FileReader(csvFile)); int count=1; String[] sample; while ((line =
	 * br.readLine()) != null) {
	 * 
	 * // use comma as separator sample = line.split(",");
	 * 
	 * if(count>71176361)
	 * System.out.println(sample[0]+","+sample[1]+","+sample[2]+","+sample[3]);
	 * 
	 * count++;
	 * 
	 * }
	 * 
	 * 
	 * br.close();
	 * 
	 * 
	 * } catch (FileNotFoundException e) { e.printStackTrace(); } catch
	 * (IOException e) { e.printStackTrace(); } finally { if (br != null) { try
	 * { br.close(); } catch (IOException e) { e.printStackTrace(); } } }
	 * 
	 * 
	 * }
	 * 
	 * 
	 */
}
