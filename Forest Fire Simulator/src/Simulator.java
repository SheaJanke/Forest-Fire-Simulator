import static java.lang.System.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


public class Simulator
{
	static ArrayList<Integer> xCor = new ArrayList<Integer>();
	static ArrayList<Integer> yCor = new ArrayList<Integer>();
	static ArrayList<String[]> grid = new ArrayList<String[]>();
	static ArrayList<String> leaked = new ArrayList<String>();
	static double adjProb = 0.2;
	static double diaProb = 0.5;
	static double leakProb = 0.95;
	static double windFactor = 0;
	static int windDirection = 0;
	static int count = 0;
	static int leaks = 0;
	static int windCount = 0;
	static int[] percent = {0,0,0,0,0,0,0,0,0,0};
	static int initialArea = 0;
	static int simulations = 1;
	
	public static void main( String args[] )
	{
		System.out.println("0: Not Burning Forest");
		System.out.println("1: Burning Forest");
		for(int a = 0; a < simulations; a++){ //Runs 500 simulations at at a time
			int[] Coordinates = {0,0,19,0,19,19,0,19}; //This is where the coordinates are entered for the simulation (x1,y1,x2,y2...)
			shape(Coordinates); 
			setGrid();
			setShape();
			if(a == 0){
				printGrid();
				initialArea = area();
				System.out.println("Area = " +area());
			}
			setRandom();
			windFactor = windStrength();
			windDirection = windDirection();
			while(isFull() == false){
				if(windCount >= 5){
					windFactor = windStrength();
					windDirection = windDirection();
					windCount = 0;
				}
				printGrid();
				spread();
				printWind();
				count++;
				windCount++;
				percentCheck();
			}
			percentPrint();
			count = 0;
			leaks = 0;
			grid.clear();
			leaked.clear();
			for(int b = 0; b < 10; b ++){
				percent[b] = 0;
			}
		}
		
	}
   
   public static void shape(int[] Coordinates){ 
	   for(int a = 0; a < Coordinates.length; a+=2){
		   xCor.add(Coordinates[a]);
	   }
	   xCor.add(Coordinates[0]);
	   for(int a = 0; a < Coordinates.length; a+=2){
		   yCor.add(Coordinates[a+1]);
	   }
	   yCor.add(Coordinates[1]);
   }
   
   public static int difference(ArrayList<Integer> test){ //returns the difference between the smallest and largest value in an arrayList
	   int smallest = test.get(0);
	   for(int a = 1; a < test.size(); a++){
		   if(test.get(a) < smallest){
			   smallest = test.get(a);
		   }
	   }
	   int largest = test.get(0);
	   for(int a = 1; a < test.size(); a++){
		   if(test.get(a) > largest){
			   largest = test.get(a);
		   }
	   }
	   
	   return largest - smallest;
	   
   }
   
   public static void setGrid(){ //creates an empty grid that is the proper size
	   for(int a = 0; a < difference(yCor) + 1; a ++){
		   String[] test = new String[difference(xCor) + 1];
		   for(int b = 0; b < difference(xCor) + 1; b++){
			   test[b] = "0";
		   }
		   grid.add(test);
	   }
	   
   }
   
   public static void setShape(){ // creates the shape in the grid that best fits the coordinates entered
	   for(int c = 0; c < xCor.size()-1; c ++){
		   double slope = 5;
		   if(xCor.get(c) != xCor.get(c+1)){
			   slope = (double)(yCor.get(c+1)- yCor.get(c))/(double)(xCor.get(c+1) - xCor.get(c));
		   }
		   if(xCor.get(c) < xCor.get(c+1)){ 
			   if(yCor.get(c+1) < yCor.get(c)){ 
				   for(int a = yCor.get(c); a >= 0; a --){
					   for(int b = xCor.get(c); b <= grid.get(0).length -1; b++){
						   if(a < (b - xCor.get(c)) * slope + yCor.get(c)){
							   String[] test = grid.get(a);
							   test[b] = " ";
							   grid.set(a, test);
						   }
					   }
				   }
			   }
			   if(yCor.get(c+1) > yCor.get(c)){ 
				   for(int a = yCor.get(c); a <= grid.size()-1; a ++){
					   for(int b = xCor.get(c); b <=  grid.get(0).length -1; b++){
						   if(a < (b - xCor.get(c)) * slope + yCor.get(c)){
							   String[] test = grid.get(a);
							   test[b] = " ";
							   grid.set(a, test);
						   }
					   }
				   }
			   }
		   }   
		   if(xCor.get(c) > xCor.get(c+1)){ 
			   if(yCor.get(c+1) < yCor.get(c)){ 
				   for(int a = yCor.get(c); a >= 0; a --){
					   for(int b = xCor.get(c); b >= 0; b--){
						   if(a > (b - xCor.get(c)) * slope + yCor.get(c)){
							   String[] test = grid.get(a);
							   test[b] = " ";
							   grid.set(a, test);
						   }
					   }
				   }
			   }
			   if(yCor.get(c+1) > yCor.get(c)){ // if the line is going down
				   for(int a = yCor.get(c); a <= grid.size()-1; a ++){
					   for(int b = xCor.get(c); b >= 0; b--){
						   if(a > (b - xCor.get(c)) * slope + yCor.get(c)){
							   String[] test = grid.get(a);
							   test[b] = " ";
							   grid.set(a, test);
						   }
					   }
				   }
			   }
		   }   
	   } 
   }
     
   public static int area(){ //returns the area of the shape
	   int area = 0;	
	   for(int a = 0; a < grid.size(); a ++){
		   for(int b = 0; b < grid.get(0).length ;b++){
			  if(grid.get(a)[b] == "0"){
					  area++;
			  }
		   }
	   }
	   return area;
   }
   
   public static void setRandom(){ //starts the fire in a random location in the shape
	   int a = (int)Math.floor(Math.random() * (grid.size()));
	   int b = (int)Math.floor(Math.random() * (grid.get(0).length));
	   if(grid.get(a)[b] == "0"){
		   grid.get(a)[b] = "1";
	   }else{
		   setRandom();
	   }
   }
   
   public static void spread(){ //makes the fire spread
	   for(int a = 0; a < grid.size(); a ++){
		   for(int b = 0; b < grid.get(0).length; b ++){
			   if(grid.get(a)[b] == "1"){
				   double prob1 = Math.random(); //determines whether the fire will spread in each of the eight directions
				   double prob2 = Math.random();
				   double prob3 = Math.random();
				   double prob4 = Math.random();
				   double prob5 = Math.random();
				   double prob6 = Math.random();
				   double prob7 = Math.random();
				   double prob8 = Math.random();
				   if(windDirection == 2){ //the probability that the fire spreads in a given direction is affected by the strength and direction of the wind
					   prob1 *= 0.7*windFactor;
					   prob2 *= windFactor;
					   prob3 *= 0.7*windFactor;
					   prob5 /= 0.7 * windFactor;
					   prob7 /= 0.7 * windFactor;
					   prob6 /= windFactor;
				   }
				   if(windDirection == 4){
					   prob3 *= 0.7*windFactor;
					   prob4 *= windFactor;
					   prob5 *= 0.7*windFactor;
					   prob1 /= 0.7 * windFactor;
					   prob7 /= 0.7 * windFactor;
					   prob8 /= windFactor;
				   }
				   if(windDirection == 6){
					   prob5 *= 0.7*windFactor;
					   prob6 *= windFactor;
					   prob7 *= 0.7*windFactor;
					   prob1 /= 0.7 * windFactor;
					   prob3 /= 0.7 * windFactor;
					   prob2 /= windFactor;
				   }
				   if(windDirection == 8){
					   prob1 *= 0.7*windFactor;
					   prob8 *= windFactor;
					   prob7 *= 0.7*windFactor;
					   prob3 /= 0.7 * windFactor;
					   prob5 /= 0.7 * windFactor;
					   prob4 /= windFactor;
				   }
				   if(windDirection == 1){
					   prob8 *= 0.7*windFactor;
					   prob1 *= windFactor;
					   prob2 *= 0.7*windFactor;
					   prob4 /= 0.7 * windFactor;
					   prob6 /= 0.7 * windFactor;
					   prob5 /= windFactor;
				   }
				   if(windDirection == 3){
					   prob2 *= 0.7*windFactor;
					   prob3 *= windFactor;
					   prob4 *= 0.7*windFactor;
					   prob6 /= 0.7 * windFactor;
					   prob8 /= 0.7 * windFactor;
					   prob7 /= windFactor;
				   }
				   if(windDirection == 5){
					   prob4 *= 0.7*windFactor;
					   prob5 *= windFactor;
					   prob6 *= 0.7*windFactor;
					   prob8 /= 0.7 * windFactor;
					   prob2 /= 0.7 * windFactor;
					   prob1 /= windFactor;
				   }
				   if(windDirection == 7){
					   prob6 *= 0.7*windFactor;
					   prob7 *= windFactor;
					   prob8 *= 0.7*windFactor;
					   prob2 /= 0.7 * windFactor;
					   prob4 /= 0.7 * windFactor;
					   prob3 /= windFactor;
				   }
				   
				   if(a > 0 && grid.get(a-1)[b] == "0"){ //top             // if the fire is not at the edge of the shape and has a high enough probability, it spreads in that direction  
					   if(prob2 > adjProb){
						   grid.get(a-1)[b] = "2";
					   }
				   }else if(a == 0 || grid.get(a-1)[b] == " "){ //top      // if the fire is at the edge of the shape and has a high enough probability, it increases the amount of help required by fire fighters
					   String spot = a-1 + " " + b;
					   if(prob2 > leakProb && leaked.contains(spot) == false){
						   leaks++;
						   leaked.add(spot);
					   }
				   }
				   if(a < grid.size()-1 && grid.get(a+1)[b] == "0"){ 
					   if(prob6 > adjProb){
						   grid.get(a+1)[b] = "2";
					   }
				   }else if(a == grid.size()-1 || grid.get(a+1)[b] == " "){ 
					   String spot = a+1 + " " + b;
					   if(prob6 > leakProb && leaked.contains(spot) == false){
						   leaks++;
						   leaked.add(spot);
					   }
				   }
				   if(b > 0 && grid.get(a)[b-1] == "0"){ 
					   if(prob8 > adjProb){
						   grid.get(a)[b-1] = "2";
					   }
				   }else if(b == 0 || grid.get(a)[b-1] == " "){ 
					   String spot = a + " " + (b-1);
					   if(prob8 > leakProb && leaked.contains(spot) == false){
						   leaks++;
						   leaked.add(spot);
					   }
				   }
				   if(b < grid.get(0).length-1 && grid.get(a)[b+1] == "0"){ 
					   if(prob4 > adjProb){
						   grid.get(a)[b+1] = "2";
					   }
				   }else if(b == grid.get(0).length-1 || grid.get(a)[b+1] == " "){ 					   String spot = a + " " + (b+1);
					   if(prob4 > leakProb && leaked.contains(spot) == false){
						   leaks++;
						   leaked.add(spot);
					   }
				   }    
				   if(a > 0 && b > 0 && grid.get(a-1)[b-1] == "0"){ //top left diagonal
					   if(prob1 > diaProb){
						   grid.get(a-1)[b-1] = "2";
					   }
				   }
				   if(a > 0 && b < grid.get(0).length -1 && grid.get(a-1)[b+1] == "0"){ 
					   if(prob3 > diaProb){
						   grid.get(a-1)[b+1] = "2";
					   }
				   }
				   if(b > 0 && a < grid.size() -1 && grid.get(a+1)[b-1] == "0"){ 
					   if(prob7 > diaProb){
						   grid.get(a+1)[b-1] = "2";
					   }
				   }
				   if(b < grid.get(0).length-1 && a < grid.size() - 1 && grid.get(a+1)[b+1] == "0"){ 
					   if(prob5 > diaProb){
						   grid.get(a+1)[b+1] = "2";
					   }
				   }
			   }
		   }
	   }
	   
	   for(int a = 0; a < grid.size(); a ++){
		   for(int b = 0; b < grid.get(0).length; b++){
			   if(grid.get(a)[b] == "2"){
				   grid.get(a)[b] = "1";
			   }
		   }
	   }
   }
   
   public static void printGrid(){ //prints the arrays in a grid format
	   System.out.println();
	   for(int a = 0; a < grid.size(); a ++){
		   System.out.println(Arrays.toString(grid.get(a)));
	   }
   }
   
   public static boolean isFull(){ //checks to see if the entire shape is on fire
	   boolean full = true;
	   for(int a = 0; a < grid.size(); a ++){
		  for(int b = 0; b < grid.get(0).length ;b++){
			  if(grid.get(a)[b] == "0"){
				  full = false;
			  }
		  }
	  }
	  return full; 
   
   }
   
   public static void percentCheck(){ //stores the number of spreads it takes for 10%, 20%, ... 100% of the shape to burn
	   for(int a = 0; a < 10; a ++){
		   if(percent[a] == 0 && (double)(initialArea - area())/(double)initialArea >= (double)(a+1)/10 ){
			   percent[a] = count;
		   }
	   }
   }
   
   public static void percentPrint(){ //prints the percentages and the amount help required by fire fighters to contain the fire within the fuel break 
	   for(int a = 0; a < 10; a ++){
		   System.out.print(percent[a] +"\t");
	   }
	   System.out.println(leaks);
   }
   
   public static double windStrength(){ // determines the strength of the wind - chooses lower values more frequently than high values
	   double wind = Math.pow(Math.random(),3) * 3 + 1;
	   return wind;
   }
   public static int windDirection(){ // determines the direction of the wind
	   int direction = (int) Math.ceil(Math.random() * 8);
	   return direction;
   }
   public static void printWind(){ //prints the strength and direction of the wind
	   if(windFactor > 2.5){
		   System.out.print("Wind Strength :: Strong ");
	   }else if(windFactor > 1.5){
		   System.out.print("Wind Strength :: Moderate ");
	   }else{
		   System.out.print("Wind Strength :: Weak ");
	   }
	   if(windDirection == 1){
		   System.out.println(" Wind Direction :: North-West");
	   }else if(windDirection == 2){
		   System.out.println(" Wind Direction :: North");
	   }else if(windDirection == 3){
		   System.out.println(" Wind Direction :: North-East");
	   }else if(windDirection == 4){
		   System.out.println(" Wind Direction :: East");
	   }else if(windDirection == 5){
		   System.out.println(" Wind Direction :: South-East");
	   }else if(windDirection == 6){
		   System.out.println(" Wind Direction :: South");
	   }else if(windDirection == 7){
		   System.out.println(" Wind Direction :: South-West");
	   }else if(windDirection == 8){
		   System.out.println(" Wind Direction :: West");
	   }
   }
}
