package ActonThreads;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author      Jacob Lashley       <chaosdeselva@gmail.com>
 * @version     1.0                 
 * @since       2014-08-04
 */
class MyRunnable implements Runnable {
    //Array List of the generated count for the threads
    ArrayList<Integer> generatorCount;
    //Array List of the generated delay for the threads
    ArrayList<Integer> generatorDelay;

    //Column Break count from input
    private int columnBreak;
    //Used to count the threads generated content
    private int threadCounter;
    //Flag to determine the column spacing
    private boolean firstColumn;

    //Lock to stop racing conditions
    private Lock lock;

    //Implemented runnable instead of extending the thread class
    MyRunnable(int num) {
        columnBreak = num;
        threadCounter = 0;
        firstColumn = false;

        generatorCount = new ArrayList<>();
        generatorDelay = new ArrayList<>();

        lock = new ReentrantLock();
    }

    //Set the delay for the threads
    public void setGeneratorDelay(int num){
        generatorDelay.add(num);
    }     

    //Set the count of content for the threads to generate
    public void setGeneratorCount(int num){
        generatorCount.add(num);
    }

    /**
    * Overriding run for the thread.  This will happen when the thread is ran.
    */
    @Override
    public void run(){
        //Gets the name of the thread
        String name = Thread.currentThread().getName();
        //Letter value to append to output so we can visually flag the thread output
        char tag = 0;
        //Index of the thread, so we can look up the delay and count for the threads
        int index = 0;

        //Switch statement to build the correct tag and index based off the name
        switch (name) {
            case "Thread-1":
                index = 0;
                tag = 'A';
                break;
            case "Thread-2":
                index = 1;
                tag = 'B';
                break;
            case "Thread-3":
                index = 2;
                tag = 'C';
                break;
            case "Thread-4":
                index = 3;
                tag = 'D';
                break;
            case "Thread-5":
                index = 4;
                tag = 'E';
                break;
            default:
                System.out.println("Error! Invalid Thread Name!");
        }

        //Loop to generate the thread content
        for ( int i = 1; i < generatorCount.get(index); i++ ){ 
            try {
                //Delay the thread between content generation
                Thread.sleep(generatorDelay.get(index));

                //Quick check for a lock when working with information else a race condition will take place causing collisions in the data updating
                if(lock.tryLock(10, TimeUnit.SECONDS)){
                    //Track the threads so we know when to break for a column
                    threadCounter++;

                    //Get the current timestamp
                    Date date = new Date();

                    //Format the date of the current timestamp
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");
                    String formattedDate = sdf.format(date);

                    //Build a string to be used in the console
                    StringBuilder sb = new StringBuilder();
                    sb.append(tag);
                    sb.append((i < 10 ? "0"+i : i));
                    String str = sb.toString();

                    //Check if this is the first in the row and use to have the correct spacing
                    if ( firstColumn == false ){
                        System.out.format("%s", formattedDate + "[" + str + "]" ); 
                        firstColumn = true;
                    } else {
                        System.out.format("%30s", formattedDate + "[" + str + "]");
                    }

                    //This will create a new line and reset the column status to false
                    if ( threadCounter % columnBreak == 0 ){
                        System.out.format("%n");
                        firstColumn = false;
                    }

                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ActonThreads.class.getName()).log(Level.SEVERE, null, ex);

            } finally{
                //release lock
                lock.unlock();
            }
        }
    }
}

/*
* The main class to run the threads program
*/
public class ActonThreads {
    
    /**
    * This is a basic random number lookup function
    *
    * @param min the lowest number the random number can lookup
    * @param max the limit the random value to lookup
    * @return send back the random number found
    */
    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    /**
    * This is the main function the runs on execution
    *
    * @param args the command line arguments
    */
    public static void main(String[] args) {
        //Check if the input is valid
        boolean isValidInput = false;
        //Store the number of threads from the input
        int numberThreads = 0;
        //Store the number of columns from the input before you break to a new line
        int numberColumns = 0;
        //Error checker used for input validation
        int errorCounter = 0;
        
        //Loop unti the input is valid
        while(!isValidInput){
            try {
                //Use the scanner class to get user input
                Scanner scanner = new Scanner(System.in);
                System.out.println("Number of threads (2 to 5): ");
                //save the input
                numberThreads = scanner.nextInt();
                
                System.out.println("Column break count (5 to 10): ");
                numberColumns = scanner.nextInt();
                
                //Check if the input is valid within the given range
                if ( numberThreads > 5 || numberThreads < 2 ){
                    System.out.println("The number of threads you have entered are not within the valid range!");
                    errorCounter++;
                }
                
                if ( numberColumns > 10 || numberColumns < 5 ){
                    System.out.println("The number of columns you have entered are not within the valid range!");
                    errorCounter++;
                }
                
                //If the error is not 0 then the input is not valid so repeat...
                if ( errorCounter > 0 ){
                    errorCounter = 0;
                    isValidInput = false;
                    
                } else {
                    //...else ithe input is valid
                    isValidInput = true;
                }

                //Create a new line
                System.out.println();
                
            } catch (Exception e) {
                //If the input is not a number then it is not valid so restart the input loop
                System.out.println("Value entered is not a number!");
                isValidInput = false;
            }
        }
        
        //Init a new runnable class
        MyRunnable runnable = new MyRunnable(numberColumns);
        
        //Keep a list of threads created
        ArrayList<Thread> threadList = new ArrayList<>();
        
        //Take the input to create threads
        for (int i = 0; i < numberThreads; i++) {
            //Create a new thread and give it a name
            Thread thread = new Thread(runnable, "Thread-"+(i+1));
            //Add the thread to the list
            threadList.add(thread);
           
            //Get the thread name back
            String name = thread.getName();
            char tag = 0;
            
            //Find the tag for the thread, this is hardcoded loop since we already know the thread range
            if (i == 0){
                tag = 'A';
            } else if (i == 1){
                tag = 'B';
            } else if (i == 2){
                tag = 'C';
            } else if (i == 3){
                tag = 'D';
            } else if (i == 4){
                tag = 'E';
            }
   
            //Generate the delay for the thead and the threads content count
            int generatorDelay = randInt(1,3)*1000;
            int generatorCount = randInt(8,25);
            
            //Store that information into the runnable array list of thread data
            runnable.setGeneratorCount(generatorCount);
            runnable.setGeneratorDelay(generatorDelay);
                     
            //Create a string ( used this method to allow char to be used ) to show the thread and the tags/content range
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(" = ");
            sb.append(tag);
            sb.append("01 to ");
            sb.append(tag);
            sb.append((generatorCount < 10 ? "0"+generatorCount : generatorCount));
            String str = sb.toString();

            System.out.println(str);  
           
        }
        
        //Create a new line
        System.out.println();
        
        //Loop all the threads now that they have been created and the list has been printed in order on the console
        for(Thread t: threadList){
            //Start all the threads
            t.start();
        }
    }
}