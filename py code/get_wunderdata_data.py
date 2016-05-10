#!/usr/bin/env python
""" get_transtat_data.py 

A quick little script to connect to RITA's ontime airline
data and pull POST requests to collect airflight performance
stats.
"""
import datetime
import requests
from os import rename
from os.path import splitext
from calendar import month_name
from httplib import HTTPConnection
from time import clock
from urllib import urlretrieve
from zipfile import ZipFile
from argparse import ArgumentParser
from csv import reader
from post import POST
import os.path

HOSTNAME = "www.wunderground.com"
TRANSTAT_URL = "http://www.wunderground.com/history/airport/K"
FREQUENCY = 1

# the available data set is generally about 3 months behind, make
# sure that we never ask for data that is within the last 3 months
MAX_DATE = datetime.date.today() + datetime.timedelta(days=-90)



def main():
    """

    """
    with open("airports.csv") as f:
    	content = f.readlines()

	for lines in content:
		airport = lines.split(",")[0]
		containsDigit = any(char.isdigit() for char in airport)
		if containsDigit == True:
			continue

		for year in range(1990,2016):
			for month in range(1,13):
				for day in range(1,32):
					print "Collecting RITA for: {}-{}/{}/{}".format(airport,day, month, year)
					get_data(airport, day, month, year)
   

    

    print "File {} written"

def get_data(airport, day, month, year, url=TRANSTAT_URL):
    """ get_data
        from month and year, create the POST
        and download the RITA data zip package
    """

    # setup the POST
   # post = post.format(monthid=month, year=year, month=month_name[month], frequency=frequency)
	
    # make sure post has no EOLs
    #post = post.replace('\n','')

    # create the user agent string, and emulate a browser
    user_agent_string = {
        "Content-Type" : "application/x-www-form-urlencoded",
        }
    
    # make a friendly name for the output file
    output_file = '{0}-{1}-{2}-{3}'.format(airport,str(day), str(month), year)
    #zip_file_name = '{}.zip'.format(output_file)
    #csv_file_name = '{}.csv'.format(output_file)
    csv_file_name = "data.csv"

    if os.path.exists(csv_file_name)==False:
    	f = open(csv_file_name,'w')
    	f.write("TimeEST,TemperatureF,Dew PointF,Humidity,Sea Level PressureIn,VisibilityMPH,Wind Direction,Wind SpeedMPH,Gust SpeedMPH,PrecipitationIn,Events,Conditions,WindDirDegrees,DateUTC\n")


    # now collect data
    #print "Collecting RITA data for {0},{1}".format(month_name[month], year)

    # lets get some data, create the request string
    #request = HTTPConnection(host)
	
    # set up the url
    url = url+airport+"/"+str(year)+"/"+str(month)+"/"+str(day)+"/DailyHistory.html?format=1";
    #url = url.format(host=host)
    print "Sending GET to {}".format(url)

    
    r = requests.get(url)
    print "get the response from the GET"
    response = r.text.replace("<br />","@").strip()
    status = r.status_code
    
    nodata = "No daily or hourly history data available"

    
     # python will convert \n to os.linesep
    if nodata not in response:
    	f = open(csv_file_name,'a')
    	lines = response.split("@")
    	i=0
    	for line in lines:
    		i = i+1
    		if len(line) > 0:
    			if i > 1:
    				f.write(line)

    	print "Collected {}".format(csv_file_name)
    	f.close()
	return csv_file_name
	

    
    
    
if __name__ == "__main__":
    
    main()
