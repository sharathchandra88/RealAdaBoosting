/**
 * 	@author Kadappanavar, Sharath Chandra Rachappa 
 * 	  
 *  
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class Realadaboosting {
   
    public static double[] fValues;
    public static double Z = 1;

    public double[] iter(double x[], int y[], double p[], int num, double epsilon) 
    {
        ErrorValmin minErrValue = probCalucate(y, p, num);
        double weakClassifier;
        double countPositive, countNegative;
        double Zt = 0;
        
        if (minErrValue.index == 0) 
        {
            weakClassifier = (x[minErrValue.index] - 1);
        } 
        else 
        {
            weakClassifier = ((double) (x[minErrValue.index] + x[minErrValue.index - 1]) / (double) 2);
        }
        System.out.println("Classifier h = " + weakClassifier);
        System.out.println("G error = " + minErrValue.ErrorVal);
        
        countNegative = (1.0 / 2.0) * Math.log((double) (minErrValue.prob[2] + epsilon) / (double) (minErrValue.prob[1] + epsilon));
        countPositive = (1.0 / 2.0) * Math.log((double) (minErrValue.prob[0] + epsilon) / (double) (minErrValue.prob[3] + epsilon));
       
        
        System.out.println("C_Plus = " + countPositive + ", " + "C_Minus = " + countNegative);
        
      
        for (int i = 0; i < num; i++) {
            if (i < minErrValue.index) {
                p[i] = p[i] * Math.pow(Math.E, (-1) * countPositive * y[i]);
            } else {
                p[i] = p[i] * Math.pow(Math.E, (-1) * countNegative * y[i]);
            }
            Zt = Zt + p[i];
        }
        System.out.println("Normalization Factor Z = " + Zt);
        System.out.print("Pi after normalization = ");
        for (int i = 0; i < num; i++) {
            p[i] = (double) p[i] / (double) Zt;
            if(i==0)
            {
            	System.out.print(p[i]);
            }
            else
            {
            System.out.print(", " + p[i] );
            }
        }
            
        System.out.println();
        
        int errors = 0;
        
        System.out.print("f(x) = ");
        for (int i = 0; i < num; i++) {
            if (x[i] < weakClassifier) {
                fValues[i] = fValues[i] + countPositive;
            } else {
                fValues[i] = fValues[i] + countNegative;
            }
            if(i==0)
            {
            System.out.print(fValues[i]);
            }
            else
            {
            	System.out.print(", " + fValues[i] );
            }
            
            if ((y[i] > 0 && fValues[i] > 0) || (y[i] < 0 && fValues[i] < 0)) {
            } else {
                errors = errors + 1;
            }
        }
        System.out.println();
        System.out.println("Boosted Classifer Error = " + (double) errors / (double) num);
        
     
        Z = Z * Zt;
        System.out.println("Bound on Error = " + Z);
        System.out.println(" ");
        return p;
    }

    public static double roundoff(double d) {
        DecimalFormat f = new DecimalFormat("#.####");
        return Double.valueOf(f.format(d));
    }

    public ErrorValmin probCalucate(int yValues[], double pValues[], int numExamples) {
        double g, ErrorValMinimum = Double.MAX_VALUE;
        char dir = 'N';
        ErrorValmin m = new ErrorValmin();        
        
        
        for (int i = 1; i < numExamples; i++) {
            double probrightPlus = 0, probrightMinus = 0, probleftPlus = 0, probleftMinus = 0;
            for (int j = 0; j < numExamples; j++) {
                if (j < i) {
                    if (yValues[j] == -1) {
                        probleftMinus = probleftMinus + pValues[j];
                    } else {
                        probrightPlus = probrightPlus + pValues[j];
                    }
                } else {
                    if (yValues[j] == -1) {
                        probrightMinus = probrightMinus + pValues[j];
                    } else {
                        probleftPlus = probleftPlus + pValues[j];
                    }
                }
            }
            g = Math.sqrt(probrightPlus * probleftMinus) + Math.sqrt(probleftPlus * probrightMinus);
            dir = 'N';
            if (g > 0.5) {
                g = 1 - g;
                double temp = probrightPlus;
                probrightPlus = probleftPlus;
                probleftPlus = temp;
                temp = probrightMinus;
                probrightMinus = probleftMinus;
                probleftMinus = temp;
                dir = 'R';
            }
            if (g < ErrorValMinimum) {
                ErrorValMinimum = g;
                m.ErrorVal = g;
                m.dir = dir;
                m.index = i;
                m.prob[0] = probrightPlus;
                m.prob[1] = probrightMinus;
                m.prob[2] = probleftPlus;
                m.prob[3] = probleftMinus;
            }
        }
        return m;
    }

    public double roundoff1(double n) {
        n = Math.round(n * 1000) / 1000.0;
        return n;
    }

    public static void main(String[] args) throws IOException {
       
        if(args.length<1)
		{
			System.out.println("Invalid Input Format");
			System.out.println("Please execute in the following format");
			System.out.println("java -jar executable.jar file_path1 file_path2");
			System.out.println("Example:");
			System.out.println("java -jar Realadaboosting.jar C:\\Users\\sharath\\Desktop\\File1.txt");
			System.exit(0);
		}
        try {
            
            ArrayList<String> inputList = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String temp;
            while ((temp = reader.readLine()) != null) {
                inputList.add(temp);
            }

            String[] firstLine = inputList.get(0).split(" ");

           
            int numIterations = Integer.parseInt(firstLine[0].trim());
            int numExamples = Integer.parseInt(firstLine[1].trim());
            double eps = Double.parseDouble(firstLine[2].trim());

            String[] xStrVal = inputList.get(1).split(" ");
            double[] xVal = new double[numExamples];
            for (int i = 0; i < numExamples; i++) {
                xVal[i] = Double.parseDouble(xStrVal[i].trim());
            }

           
            String[] yStrVal = inputList.get(2).split(" ");
            int[] yVal = new int[numExamples];
            for (int i = 0; i < numExamples; i++) {
                yVal[i] = Integer.parseInt(yStrVal[i].trim());
            }

           
            String[] pStrVal = inputList.get(3).split(" ");
            double[] probVal = new double[numExamples];
            for (int i = 0; i < numExamples; i++) {
                probVal[i] = Double.parseDouble(pStrVal[i].trim());
            }

            Realadaboosting realboosting = new Realadaboosting ();
            System.out.println("Kadappanavar, Sharath Chandra Rachappa");
            System.out.println("");
            fValues = new double[numExamples];

            for (int num = 0; num < numExamples; num++) {
                fValues[num] = 0;
            }
            for (int r = 0; r < numIterations; r++) {
                System.out.println("Iteration " + (r + 1));
                //System.out.println("--------------------------------------------");
                probVal = realboosting.iter(xVal, yVal, probVal, numExamples, eps);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

       
    }

    class ErrorValmin {

        public double prob[] = new double[4];
        public char dir;
        public double ErrorVal;
        public int index;
    }

}
