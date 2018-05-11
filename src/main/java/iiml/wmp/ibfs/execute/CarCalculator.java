package iiml.wmp.ibfs.execute;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import iiml.wmp.ibfs.beans.ScreenerBean;
import iiml.wmp.ibfs.car.CarBean;
import iiml.wmp.ibfs.utils.TestUtils;

public class CarCalculator {
    public static void main(String[] args) throws Exception{
        getFiles();
        Date date = new SimpleDateFormat("yyyyMMdd").parse("20120227");


//        DateUtils.parseDate()
//        System.out.println(date);


    }






    private static void getFiles() throws Exception{
        String[] arr = { "json" };
        Collection<File> files = FileUtils.listFiles(new File("output"), arr, false);
        List<ScreenerBean> beans = new ArrayList<>();
        List<String> missingList = new ArrayList<>();
        List<String> nonMissingList = new ArrayList<>();
        for (File file : files)
        {
            ScreenerBean sb = TestUtils.gson.fromJson(FileUtils.readFileToString(file, StandardCharsets.UTF_8), ScreenerBean.class);
//            generateCarMap(sb,-2000,-1,"Announcement",2000);
//            generateCarMap(sb,-2000,-1,"ExRights",2000);
            Map<Date,Double> stockPricesMap = combineAllPrices(sb);
            Date announcedDate = Application.getDateFormat(sb.getExcelBean().getAnnounced());
            Date exRightsDate = Application.getDateFormat(sb.getExcelBean().getEx_Rights());
            Date recordDate = Application.getDateFormat(sb.getExcelBean().getRecord());

//            System.out.println(sb.getExcelBean().getCompany());
            Map<Date,Double> combined = combineAllPrices(sb);
//            System.out.println(combined.get(announcedDate));
//            System.out.println(combined.get(exRightsDate));
//            System.out.println(combined.get(recordDate));

//            System.out.println("************************");
            if(combined.get(announcedDate)== null||
                    combined.get(exRightsDate)==null||
                    combined.get(recordDate)==null){
                missingList.add(sb.getExcelBean().getCompany());
            }
            else {

                Map<String,Map<Date,Double>> splitMapAnnounced = getEstimationEventSplitBackward(combined,announcedDate,21,203);
                Map<Date,Double> postEventAnnounced=getPostEventData(combined,announcedDate,20);
                Map<String,Map<Date,Double>> splitMapExRights = getEstimationEventSplitBackward(combined,exRightsDate,21,203);
                Map<Date,Double> postEventExRights=getPostEventData(combined,exRightsDate,20);;
                Map<String,Map<Date,Double>> splitMapRecord = getEstimationEventSplitBackward(combined,recordDate,21,203);
                Map<Date,Double> postEventRecord=getPostEventData(combined,recordDate,20);
                Double announcedPrice = combined.get(announcedDate);
                Double exRightsPrice = combined.get(exRightsDate);
                Double recordPrice = combined.get(recordDate);

                ArrayList<Integer> sizes = new ArrayList<>();
                sizes.add(postEventAnnounced.keySet().size());
                sizes.add(postEventExRights.keySet().size());
                sizes.add(postEventRecord.keySet().size());
                sizes.add(splitMapAnnounced.get("Event").keySet().size());
                sizes.add(splitMapExRights.get("Event").keySet().size());
                sizes.add(splitMapRecord.get("Event").keySet().size());

//                System.out.println(postEventAnnounced.keySet().size());
//                System.out.println(postEventExRights.keySet().size());
//                System.out.println(postEventRecord.keySet().size());
//                System.out.println(splitMapAnnounced.get("Event").keySet().size());
//                System.out.println(splitMapExRights.get("Event").keySet().size());
//                System.out.println(splitMapRecord.get("Event").keySet().size());
//                System.out.println(splitMapAnnounced.get("Estimation").keySet().size());
//                System.out.println(splitMapExRights.get("Estimation").keySet().size());
//                System.out.println(splitMapRecord.get("Estimation").keySet().size());


                boolean good = true;

                Collections.sort(sizes);
                if(sizes.get(0) >= 20){
                    sizes = new ArrayList<>();
                    sizes.add(splitMapAnnounced.get("Estimation").keySet().size());
                    sizes.add(splitMapExRights.get("Estimation").keySet().size());
                    sizes.add(splitMapRecord.get("Estimation").keySet().size());
                    Collections.sort(sizes);
                    if(sizes.get(0) <105){
                        good = false;
//                        System.out.println("Estimation:"+sizes.get(0));
                    }
                }
                else {
//                    System.out.println("Event:"+sizes.get(0));
                    good = false;
                }



                if(good) {
                    nonMissingList.add(sb.getExcelBean().getCompany());
                    Map<Integer,Double> announcedEstimationCar = getEstimationCarMap(splitMapAnnounced.get("Estimation"));
                    Map<Integer,Double> announcedEventCar = getEventCarMap(splitMapAnnounced.get("Event"),announcedPrice,postEventAnnounced);

                    Map<Integer,Double> exRightsEstimationCar = getEstimationCarMap(splitMapExRights.get("Estimation"));
                    Map<Integer,Double> exRightsEventCar = getEventCarMap(splitMapExRights.get("Event"),exRightsPrice,postEventExRights);

                    Map<Integer,Double> recordEstimationCar = getEstimationCarMap(splitMapRecord.get("Estimation"));
                    Map<Integer,Double> recordEventCar = getEventCarMap(splitMapRecord.get("Event"),recordPrice,postEventRecord);

                    CarBean carBean = new CarBean(sb,
                            announcedEventCar,
                            announcedEstimationCar,
                            recordEventCar,
                            recordEstimationCar,
                            exRightsEventCar,
                            exRightsEstimationCar);

                    System.out.println("*******************************************");

                }
                else
                    missingList.add(sb.getExcelBean().getCompany());

            }

//            generateCarMap(sb,-300,100,"Record",400);

        }
//        System.out.println("Missing:"+missingList.size());
//        for(String s:missingList){
//            System.out.println(s);
//        }
//        System.out.println("************************");
//        System.out.println("Non Missing:"+nonMissingList.size());
//        for(String s:nonMissingList){
//            System.out.println(s);
//        }

    }

    private static Map<Integer,Double> getEstimationCarMap(Map<Date,Double> inputMap){
        Map<Integer,Double> estimationMap = new TreeMap<>();
        Integer index = -inputMap.keySet().size()-21;
        for(Date d:inputMap.keySet()){
            estimationMap.put(index,inputMap.get(d));
            index++;
        }
        return estimationMap;
    }

    private static Map<Integer,Double> getEventCarMap(Map<Date,Double> preEvent,Double eventDay,Map<Date,Double> postEvent){
        Map<Integer,Double> eventMap = new TreeMap<>();
        Integer index = -preEvent.keySet().size();
        for(Date d:preEvent.keySet()){
            eventMap.put(index,preEvent.get(d));
            index++;
        }
        eventMap.put(0,eventDay);
        index = 1;
        for(Date d:postEvent.keySet()){
            eventMap.put(index,postEvent.get(d));
            index++;
        }
        return eventMap;
    }

    private static Map<String,Map<Date,Double>> getEstimationEventSplitBackward(Map<Date,Double> combined,
                                                                                Date eventDate,
                                                                                Integer eventSize,
                                                                                Integer maxEstimation) throws Exception {
        Map<String,Map<Date,Double>> split = new HashMap<>();
        Map<Date,Double> beforeEvent = new TreeMap<>();
        for(Date d:combined.keySet()){
            if(d.before(eventDate)){
                beforeEvent.put(d,combined.get(d));
            }
        }
        if(beforeEvent.size() < eventSize){
            throw new Exception("Insufficient number of prices");
        }
        Integer start = beforeEvent.size() - maxEstimation;
        Integer count = beforeEvent.size() - eventSize,i=0;
        Map<Date,Double> estimation = new TreeMap<>();
        Map<Date,Double> event = new TreeMap<>();
        boolean begin = false;
        for(Date d:beforeEvent.keySet()){
            if(begin) {
                if (i < count) {
                    estimation.put(d, beforeEvent.get(d));
                } else {
                    event.put(d, beforeEvent.get(d));
                }
            }
            i++;
            if(i>start)
                begin = true;
        }
        split.put("Estimation",estimation);
        split.put("Event",event);
        return split;
    }

    private static Map<Date,Double> getPostEventData(Map<Date,Double> combined,
                                                     Date eventDate,
                                                     Integer eventSize){
        Map<Date,Double> postEventData = new TreeMap<>();
        int count = 0;
//        for()
        for(Date d:combined.keySet()){
            if(eventDate.before(d)){
                postEventData.put(d,combined.get(d));
                count++;
                if(count >= eventSize)
                    break;
            }
        }
        return postEventData;
    }


    private static Map<Date,Double> getDateMap(Map<String,Double> stringMap) throws Exception{
        Map<Date,Double> toReturn = new HashMap<>();
        for(String s:stringMap.keySet()){
            toReturn.put(new SimpleDateFormat("yyyyMMdd").parse(s),stringMap.get(s));
        }
        return toReturn;
    }

    public static Map<Date,Double> combineAllPrices(ScreenerBean screenerBean) throws Exception{
        Map<Date,Double> combined = new TreeMap<>();
        Map<Date,Double> announced = getDateMap(screenerBean.getStockPricesEventDays().getAnncStocks());
        Map<Date,Double> exRights = getDateMap(screenerBean.getStockPricesEventDays().getExRightsStocks());
        Map<Date,Double> record = getDateMap(screenerBean.getStockPricesEventDays().getRecordStocks());


        combined.putAll(announced);
        combined.putAll(exRights);
        combined.putAll(record);
        return combined;
    }





    private static void generateCarMap(ScreenerBean screenerBean, Integer start, Integer end, String type, Integer numberOfDays) throws Exception{
        Map<String,Double> stringMap;
        Date referenceDate;
        System.out.println(screenerBean.getExcelBean().getCompany());
        if(type.equals("Announcement")){
            stringMap = screenerBean.getStockPricesEventDays().getAnncStocks();
            referenceDate = Application.getDateFormat(screenerBean.getExcelBean().getAnnounced());
        }
        else if(type.equals("ExRights")){
            stringMap = screenerBean.getStockPricesEventDays().getExRightsStocks();
            referenceDate = Application.getDateFormat(screenerBean.getExcelBean().getEx_Rights());
        }
        else if(type.equals("Record")){
            stringMap = screenerBean.getStockPricesEventDays().getRecordStocks();
            referenceDate = Application.getDateFormat(screenerBean.getExcelBean().getRecord());
        }
        else{
            throw new Exception("Invalid type");
        }
//        if(referenceDate.getYear() < 2000){
//            referenceDate.setYear(referenceDate.getYear()+2000);
//        }
        System.out.println("Reference Date:"+referenceDate);
        Integer curInd = 1;
        Map<Date,Double> dateDoubleMap = getDateMap(stringMap);

        Map<Long,Double> daywiseReturnMap = new TreeMap<>();
        for(Integer i=end;i>start;i--) {
            Date currentDate = DateUtils.addDays(referenceDate, i);
            if (dateDoubleMap.get(currentDate) != null) {
                Long days = ChronoUnit.DAYS.between(currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        referenceDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                daywiseReturnMap.put(days,dateDoubleMap.get(currentDate));
                curInd++;
//                System.out.println(currentDate + ":"+dateDoubleMap.get(currentDate));
            }
            if(curInd > numberOfDays){
                break;
            }

        }
        for(Long i:daywiseReturnMap.keySet()){
            System.out.println(i.toString()+":"+daywiseReturnMap.get(i));
        }
        System.out.println("Number of entries:"+String.valueOf(curInd-1));

    }
}
