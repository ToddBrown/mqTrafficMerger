//Written by Todd Brown of Parasoft Corporation 5/7/2015
//Version 1.1.2

package com.parasoft.MqTrafficMerge;

import java.io.*;

public class MqTrafficMerge
  {
      public static void main(String[] args) throws IOException
      {
    	  String outputFile = "";
    	  BufferedWriter outputWriter = null;
    	  BufferedReader reader = null;
    	  //Define output file Location
    	  try
    	  {
    		  System.out.println("\n\t\"s are not required for any inputs to this program. \nWrite output file location (Default: "
    		  		+ System.getProperty("user.dir") + "\\mergedTraffic.txt):");
    		  reader = new BufferedReader(new InputStreamReader(System.in));
    		  outputFile = reader.readLine();
    		  outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
    	  }
    	  //catch invalid file paths and default to current directory traffic file
    	  catch (FileNotFoundException fileNotFound)
    	  {
    		  System.err.println("File path is not valid!\n\nDefaulting to file: " + System.getProperty("user.dir") + "\\mergedTraffic.txt");
    		  outputFile = System.getProperty("user.dir") + "\\mergedTraffic.txt";
    	  }
    	  //close out writer from try block if still open
    	  finally
    	  {
    		  if (outputWriter != null)
    		  {
    			  outputWriter.close();
    		  }
    	  }
    	  
    	  //open newly created/defined file for writing
    	  BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
    	  
    	  //Define and read in location of traffic files to be merged
    	  String trafficDirectory = "";
          System.out.println("\n\nProvide directory that houses traffic files to be merged.\nIf no directory is provided, we will default to: " +
        		  		      System.getProperty("user.dir") + "\n\nDesired location of traffic files:");
          reader = new BufferedReader(new InputStreamReader(System.in));
          String check = reader.readLine();
          if (check.isEmpty())
          {
        	  trafficDirectory = System.getProperty("user.dir");
        	  System.out.println("Defaulting to: " + System.getProperty("user.dir") + '\n');
          }
          else
          {
        	  trafficDirectory = check;
        	  System.out.println("Using provided directory of:" + trafficDirectory);
          }
          reader.close();
          
          //write standard headers
          writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
          writer.write("<root xmlVersion=\"5\">\n");
          writer.write("<MQTrafficSerializer className=\"webtool.test.wizard.mq.MQTrafficSerializer\">\n");
          
          
          String inputFileLine = "";
          int maxSize = 0;
          
          //Parse each file in given directory
          BufferedReader readerFiles = null;
          File dir = new File(trafficDirectory);
          File[] directoryListing = dir.listFiles();
          if (directoryListing != null) 
          {
        	//search for line that contains mqMessages
            for (File child : directoryListing) 
            {
              String typeCheck = child.toString();
              if (!typeCheck.contains(".txt") && !typeCheck.contains(".xml"))
              {
            	  System.out.println("Skipping file " + typeCheck + " because it is not a .txt or .xml\n");
            	  if (readerFiles != null)
            	  {
            		  readerFiles.close();
            	  }
              }
              else
              {
            	  readerFiles = new BufferedReader(new InputStreamReader(new FileInputStream(child), "UTF-8"));
            	  do
            	  {
            		  inputFileLine = readerFiles.readLine();
            	  } while(inputFileLine !=null && !inputFileLine.contains("mqMessages size="));
              
            	  //fail-over if a files contain mqMessages element
            	  if (inputFileLine == null)
            	  {
            		  System.out.println("Writer exited the file " + child + " because mqMessages is nonexistant.\n");
            		  readerFiles.close();
            		  //writer.close();
            		  //return;
            	  }
              
            	  //strip out everything but size value in line containing mqMessages
            	  else
            	  {
            		  inputFileLine = inputFileLine.replaceAll("<mqMessages size=\"","");
            		  inputFileLine = inputFileLine.replaceAll("\">","");
            		  inputFileLine = inputFileLine.replaceAll("\\s","");
            		  maxSize = maxSize + Integer.valueOf(inputFileLine);
            		  readerFiles.close();
            	  }
              }
            }
            if (maxSize == 0)
            {
            	System.out.println("\n\nNo files contained mqMessages in the specified directory. Exiting!");
            	writer.close();
            	return;
            }
          }
          
          else 
          {
        	  // Handle the case where dir is not really a directory.
        	  System.out.println("Directory specified doesn't exist or isn't a directory.");
        	  writer.close();
        	  return;
          }
          
          /*
           	  String maxSize = Integer.toString(size);
              int numOfZeros = maxSize.length - 10;
              String zero = "0";
              for (int i = 0; i < numOfZeros; i++)
              {
                  maxSize = zero.concat(maxSize);
              }
          */
          
          
          //write into specified output file
          String mQMessageTag = "<mqMessages size=\"";
          mQMessageTag = mQMessageTag.concat(Integer.toString(maxSize));
          mQMessageTag = mQMessageTag.concat("\">\n");
          writer.write(mQMessageTag);
          inputFileLine = "";
          //int index = 0;
          BufferedReader readerNewFile = null;
          if (directoryListing != null) 
          {
        	//search for line that contains mqMessages
            for (File child : directoryListing) 
            {
              String typeCheck = child.toString();
              if (!typeCheck.contains(".txt") && !typeCheck.contains(".xml"))
              {
            	  readerNewFile.close();
              }
              else
              {
            	  readerNewFile = new BufferedReader(new InputStreamReader(new FileInputStream(child), "UTF-8"));
            	  System.out.println("Currently processing: " + child);
                  while ((inputFileLine = readerNewFile.readLine()) != null)
                  {
                      /*if (inputFileLine.contains("MQMessageSerializer className="))
                      {
                    	  writer.write("<MQMessageSerializer className=\"com.parasoft.net.mq.MQMessageSerializer\" index=\"" + index + 
                    			  "\" version=\"2\"");
                    	  index++;
                    	  writer.write("\n");
                      }*/
                      if (!inputFileLine.equals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>") && !inputFileLine.equals("<root xmlVersion=\"5\">")
                       && !inputFileLine.contains("<MQTrafficSerializer className=\"webtool.test.wizard.mq.MQTrafficSerializer\">") && !inputFileLine.contains("</mqMessages>")
                       && !inputFileLine.contains("</MQTrafficSerializer>") && !inputFileLine.equals("</root>") && !inputFileLine.contains("<mqMessages size=\""))
                      {
                          writer.write(inputFileLine);
                          writer.write("\n");
                      }
                  }
                  readerNewFile.close();
                }
              }
          }
          
          //write closing tags into output file
          writer.write("</mqMessages>\n");
          writer.write("</MQTrafficSerializer>\n");
          writer.write("</root>\n");
          
       // Close to unlock and flush to disk.
          writer.close();
          
          System.out.println("\n\nFiles merged succesfully! Merged traffic resides at:\n" + outputFile);

      }
          
  }