package com.easycompany.trappd.util;

public class SampleDataSupplier {

  public static String sampleCaseDto() {
    return "{\"Patient Number\":\"1\",\"State Patient Number\":\"KL-TS-P1\",\"Date Announced\":\"30/01/2020\",\"Age Bracket\":\"20\",\"Gender\":\"F\",\"Detected City\":\"Thrissur\",\"Detected District\":\"Thrissur\",\"Detected State\":\"Kerala\",\"State code\":\"KL\",\"Current Status\":\"Recovered\",\"Notes\":\"Travelled from Wuhan\",\"Contracted from which Patient (Suspected)\":\"\",\"Nationality\":\"India\",\"Type of transmission\":\"Imported\",\"Status Change Date\":\"14/02/2020\",\"Source_1\":\"https://twitter.com/vijayanpinarayi/status/1222819465143832577\",\"Source_2\":\"https://weather.com/en-IN/india/news/news/2020-02-14-kerala-defeats-coronavirus-indias-three-covid-19-patients-successfully\",\"Source_3\":\"Student from Wuhan\",\"Backup Notes\":\"\",\"\":\"\"}";
  }
  public static String sampleCaseDtoV2() {
    return "{\n"
        + "\t\t\t\"agebracket\": \"20\",\n"
        + "\t\t\t\"backupnotes\": \"Student from Wuhan\",\n"
        + "\t\t\t\"contractedfromwhichpatientsuspected\": \"\",\n"
        + "\t\t\t\"currentstatus\": \"Recovered\",\n"
        + "\t\t\t\"dateannounced\": \"30/01/2020\",\n"
        + "\t\t\t\"detectedcity\": \"Thrissur\",\n"
        + "\t\t\t\"detecteddistrict\": \"Thrissur\",\n"
        + "\t\t\t\"detectedstate\": \"Kerala\",\n"
        + "\t\t\t\"estimatedonsetdate\": \"\",\n"
        + "\t\t\t\"gender\": \"F\",\n"
        + "\t\t\t\"nationality\": \"India\",\n"
        + "\t\t\t\"notes\": \"Travelled from Wuhan\",\n"
        + "\t\t\t\"patientnumber\": \"1\",\n"
        + "\t\t\t\"source1\": \"https://twitter.com/vijayanpinarayi/status/1222819465143832577\",\n"
        + "\t\t\t\"source2\": \"https://weather.com/en-IN/india/news/news/2020-02-14-kerala-defeats-coronavirus-indias-three-covid-19-patients-successfully\",\n"
        + "\t\t\t\"source3\": \"\",\n"
        + "\t\t\t\"statecode\": \"KL\",\n"
        + "\t\t\t\"statepatientnumber\": \"KL-TS-P1\",\n"
        + "\t\t\t\"statuschangedate\": \"14/02/2020\",\n"
        + "\t\t\t\"typeoftransmission\": \"Imported\"\n"
        + "\t\t}";
  }
}
