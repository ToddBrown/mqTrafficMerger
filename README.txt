README

mqTrafficMerger V1

Merges multiple IBM MQ traffic files together into one large file in a memory efficient manner.

The .jar can be execute on any machine with Java 7 or higher. It can be executed by double clicking the .jar if .jar's are set to be opened using a javaw process or via command line via the command: java -jar MqTrafficMerger.jar

You will be prompted for two inputs after executing the .jar: 
												a specification of a file that will be the target of the outputted and merged traffic
												and
												a specification of the directory in which the traffic file's you'd like merged reside

Both of these values can default without input if desired.
												Default Value for the output file is: 		<directory/of/jar/>mergedTraffic.txt
												Default Value for the traffic directory:	<directory/of/jar>

If passing specific values quotations are not required, even if the path contains spaces.