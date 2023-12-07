//Uditi Madan, 12/06/23

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;


class IRoadTrip {

    // Declaring hashmaps 
    HashMap<String, String> countryCode; // fixing country names to ids
    HashMap<String, Integer> distances; 
    HashMap<String, ArrayList<String>> adjacents; // stateids hashmap from borders.txt
    HashMap<String, HashMap<String, Integer>> bighash;

    //File Readers
    String borders; 
    String capdist; 
    String state_name; 

    String country1;
    String country2;

    // Boolean Val to exit out of the program
    boolean exit = false;

    // IRoadTrip Constructor 
    public IRoadTrip(String[] args) throws FileNotFoundException {

        // Reading file names from args
        borders = args[0]; 
        capdist = args[1]; 
        state_name = args[2]; 

        // Methods to build Data structures from the given files
        countryCodeMap(state_name);
        distanceMap(capdist);
        adjacentBuilder(borders);
        hashMap(adjacents);
    }

   //HashMap with ids as keys and an arraylist of HashMaps "smallhash"s as values
   //smallhash holds id as key and km distance between captitols of countries as a value
    void hashMap(HashMap<String, ArrayList<String>> adjacents) {

        bighash = new HashMap<String, HashMap<String, Integer>>();
        for (String mainKey : countryCode.values()) {

            HashMap<String, Integer> smallhash = new HashMap<String, Integer>(); 
            ArrayList<String> borderingcountries = adjacents.get(mainKey);

            if (borderingcountries == null) { //No border logic
                smallhash = null;
            } else { // there are bordering countries
                for (String c : borderingcountries) { // build smallhashes
                    String distanceKey = mainKey + c; 
                    Integer km = distances.get(distanceKey); // km between capitols
                    smallhash.put(c, km);
                }
            }
            bighash.put(mainKey, smallhash);
        }
    }

    void adjacentBuilder(String borders) throws FileNotFoundException {

        //Border.txt file reader
        File filer = new File(borders);
        Scanner scan = new Scanner(filer);

        // adjacent country state_ids Hashmap
        adjacents = new HashMap<String, ArrayList<String>>(); 
        //Main key value
        String mainKey; 

        while (scan.hasNextLine()) {
            String nextline = scan.nextLine();
            nextline = nextline.trim();

            //Split new array with required values
            String[] adjacentArray = nextline.split("="); 
            //Adjacent key
            mainKey = adjacentArray[0].trim(); 
            //Border Array 
            ArrayList<String> bordering = new ArrayList<String>(); 

            if (!countryCode.containsKey(mainKey)) { 

                if (mainKey.contains("(")) { 
                    String[] names = mainKey.split("\\(");

                    // Trimming ")" and white space
                    String name1 = names[0].trim();
                    String name2 = names[1].substring(0, names[1].length() - 1).trim();

        
                    if (!countryCode.containsKey(name1) && !countryCode.containsKey(name2)) {
                        // Ignoring over the unrecognized country
                        mainKey = "ignore"; 
                    } 
                    else if (countryCode.containsKey(name1)) {
                        mainKey = countryCode.get(name1);
                    } 
                    else if (countryCode.containsKey(name2)) {
                        mainKey = countryCode.get(name2);
                    }
                } else 
                { 
                    mainKey = "ignore"; }

            //Valid Countries
            } else { 
                mainKey = countryCode.get(mainKey); 
            }

            if (adjacentArray.length < 2) { 
                bordering = null;
            } //Bordering Countries 
            else { 
                String[] arn = adjacentArray[1].split(";");
                for (String x : arn) { 
                    
                    String[] elem = x.split(" ");
                    String element = "";
                    for (int i = 0; i < elem.length - 3; i++) {
                        element += elem[i] + " ";
                    }
                    // Space Check-- end of the country
                    element += elem[elem.length - 3]; 
                    element = element.trim();
                   
                    //Country not in the state_name.tsv file
                    if (!countryCode.containsKey(element)) { 
                        if (element.contains("(")) { 
                            String[] adjacentArray2 = element.split("\\(");
                         
                            String adj1 = adjacentArray2[0].trim();
                            String adj2 = adjacentArray2[1].substring(0, adjacentArray2[1].length() - 1).trim();
                          
                            if (!countryCode.containsKey(adj1) && !countryCode.containsKey(adj2)) { 
                                element = "ignore"; 
                            } else if (countryCode.containsKey(adj1)) {
                                element = countryCode.get(adj1);
                            } else if (countryCode.containsKey(adj2)) {
                                element = countryCode.get(adj2);
                            }
                        } // Unrecognized Borders
                        else {
                            element = "ignore";
                        }
                    } else { 
                        element = countryCode.get(element);
                    }
                    if (element != "ignore") {
                        bordering.add(element); 
                    }
                }
            }
            if (mainKey != "ignore") {
                adjacents.put(mainKey, bordering);
            }
        }
    }


    // Country Hashmap with Country names as keys and their Stateids as values
    void countryCodeMap(String state_name) throws FileNotFoundException {
        File file = new File(state_name);
        Scanner scan = new Scanner(file);

        //New HashMap connecting country to their id
        countryCode = new HashMap<String, String>();

        while (scan.hasNextLine()) {
            String nextline = scan.nextLine();
            
            //Valid country with respect to the date
            if (nextline.contains("2020-12-31")) {
                String[] newArray = nextline.split("\t");
                String state1;
                String id = newArray[1];
        
                if (newArray[2].contains("(")) {
                    String[] countryArray = newArray[2].split("\\(");
                    state1 = countryArray[0].trim();
                    String state2 = countryArray[1].substring(0, countryArray[1].length() - 1).trim();
                   
                    countryCode.put(state1, id);
                    countryCode.put(state2, id);
                } else {
                    state1 = newArray[2].trim();
                    
                    countryCode.put(state1, id);
                }
            }
        }
        // Fixing country names 
        countryCode.put("Bahamas, The", "BHM");
        countryCode.put("Cabo Verde", "CAP");
        countryCode.put("Bosnia and Herzegovina", "BOS");
        countryCode.put("Congo, Democratic Republic of the", "DRC");
        countryCode.put("Congo, Republic of", "CON");
        countryCode.put("Czechia", "CZR");
        countryCode.put("Eswatini", "SWA");
        countryCode.put("Gambia, The", "GAM");
        countryCode.put("Germany", "GFR");
        countryCode.put("Italy", "ITA"); 
        countryCode.put("Sardinia", "ITA");
        countryCode.put("Korea, North", "PRK");
        countryCode.put("Korea, South", "ROK");
        countryCode.put("North Macedonia", "MAC");
        countryCode.put("United States", "USA");
        countryCode.put("Vietnam", "DRV");
        countryCode.put("Suriname", "SUR");
        countryCode.put("Tanzania", "TAZ"); 
        countryCode.put("Tanganyika", "TAZ");
        countryCode.put("UK", "UK");
        countryCode.put("United Kingdom", "UK");
        countryCode.put("East Timor", "ETM");
        countryCode.put("Timor-Leste", "ETM");
        countryCode.put("Romania", "RUM");

        // Name corrections
        countryCode.put("The Bahamas", "BHM");
        countryCode.put("Republic of Congo", "CON");
        countryCode.put("Democratic Republic of Congo", "DRC");
        countryCode.put("The United States", "USA");
        countryCode.put("US", "USA");
        countryCode.put("USA", "USA");
        countryCode.put("The United Kingdom", "UK");
        

        //Removing countries not in capdist.csv file
        countryCode.remove("South Sudan");
        countryCode.remove("Kosovo");
    }

    // HashMap distances using country names as key and km as value
    void distanceMap(String capdist) throws FileNotFoundException {
        //New Hashmap
        distances = new HashMap<String, Integer>();

        File filer = new File(capdist);
        Scanner scanner = new Scanner(filer);
        scanner.nextLine();

        while (scanner.hasNextLine()) {
            String nextline = scanner.nextLine();
            String[] newArray2 = nextline.split(",");
            String jointArray = newArray2[1] + newArray2[3];
            Integer km = Integer.parseInt(newArray2[4]);
            distances.put(jointArray, km);
        }
    }

  
    public int getDistance(String country1, String country2) {
        // uses bighash table to check they border eachother 
        if (bighash.containsKey(country1) && bighash.get(country1).containsKey(country2)) {

            Integer km = bighash.get(country1).get(country2);
            //returning km between capitols
            int kilometer = km; 
            return kilometer;
        }
        return -1;
    }

    //dijkstra algo to find the shortest path distance
    public List<String> findPath(String country1, String country2) {
        
        HashMap<String, String> parents = algorithm(country1); // New Hashmap of parent countries for shortest paths
        boolean nopath = false; // No path found
        List<String> path = new ArrayList<String>(); // List---countries travelled

        if (country1.equals(country2)) { // first and second country is the same
            String country = countryIDReturn(country1);
            path.add(country + " --> " + country + " (0 km)");
        }//Distance is zero as we are already there!

        String current = country2;
        while (current != null && !current.equals(country1)) {
            
            String countryName1 = countryIDReturn(parents.get(current));
            String countryName2 = countryIDReturn(current);
            //Island check or invalid country
            if (countryName1 == null) { 
                nopath = true;
                break; 
            }
            
            path.add(countryName1 + " --> " + countryName2 + " (" + distances.get(parents.get(current) + current) + " km)");
            current = parents.get(current); 
        }
        Collections.reverse(path); // Start to finish path

        //Island-- empty path
        if (nopath) { 
            path.clear();
            return path;
        }
        return path;
    }

    // Id returns out the name of a country 
    String countryIDReturn(String id) {
        for (Map.Entry<String, String> entry : countryCode.entrySet()) {
            if (entry.getValue().equals(id)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Dijsktra's algorithm used to find the shortest path
    HashMap<String, String> algorithm(String country1) {

    
        HashMap<String, Integer> countryPathways = new HashMap<String, Integer>(); // New hashmap storing countries with shortest distance
        HashSet<String> countryVisited = new HashSet<String>(); // New hashmap storing the already visited countries
        PriorityQueue<String> newNextCountry = new PriorityQueue<String>(Comparator.comparing(countryPathways::get));  // shortest travel distance --New priority queue with adjacent countries
        HashMap<String, String> parentCheck = new HashMap<String, String>(); // New hashmap to check and store the parent countries

        for (String country : bighash.keySet()) {
            countryPathways.put(country, Integer.MAX_VALUE);
        }
        // first country=distance 0
        //nect country with its corresponding distance
        countryPathways.put(country1, 0);
        newNextCountry.add(country1);

        // Algorithm Logic
        while (!newNextCountry.isEmpty()) {
            String current = newNextCountry.poll(); 
            if (countryVisited.contains(current)) {
                continue;
            } 
            if (countryVisited.contains(this.country2)) {
                break; 
            }
            countryVisited.add(current);
            //Island check---No
            if (bighash.get(current) != null) { 
                for (String adj : bighash.get(current).keySet()) {
                    if (!countryVisited.contains(adj)) { 
                        Integer kmbetween = bighash.get(current).get(adj); 
                        int kmfromcurr = countryPathways.get(current) + kmbetween; 
                        if (kmfromcurr < countryPathways.get(adj)) { 
                            countryPathways.put(adj, kmfromcurr); 
                            parentCheck.put(adj, current); 
                            newNextCountry.add(adj); 
                        }
                    }
                }
            }
        }
        return parentCheck;
    }

    //Handles the user inputs
    public void acceptUserInput() {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the first country (Type EXIT to quit): ");
        String firstCountry = scanner.nextLine();
        
        while (!firstCountry.equalsIgnoreCase("EXIT")
                && (!countryCode.containsKey(firstCountry))) {
            System.out.println("Invalid country name. Please enter a valid country name.");
            System.out.print("Enter the name of the first country (Type EXIT to quit): ");
            firstCountry = scanner.nextLine();
        }
        if (firstCountry.equalsIgnoreCase("EXIT")) {
            exit = true; 
        } else {
            country1 = countryCode.get(firstCountry); 
            System.out.print("Enter the name of the second country (Type EXIT to quit): ");
            String secondCountry = scanner.nextLine();
            
            while (!secondCountry.equalsIgnoreCase("EXIT")
                    && (!countryCode.containsKey(secondCountry))) {
                System.out.println("Invalid country name. Please enter a valid country name.");
                System.out.print("Enter the name of the second country (Type EXIT to quit): ");
                secondCountry = scanner.nextLine();
            }
            if (secondCountry.equalsIgnoreCase("EXIT")) {
                exit = true; 
            } else {
                country2 = countryCode.get(secondCountry); 
            }
        }
    }
    public static void main(String[] args) throws FileNotFoundException {
        IRoadTrip a3 = new IRoadTrip(args);
        while (!a3.exit) {
            System.out.println();
            a3.acceptUserInput();
            if (!a3.exit) {
                List<String> path = a3.findPath(a3.country1, a3.country2);
                if (path.isEmpty()) {
                    System.out.println("no path found");
                } else {
                    System.out.println();
                    for (String travel : path) {
                        System.out.println(travel);
                    }
                }
            }
        }
    }
}
