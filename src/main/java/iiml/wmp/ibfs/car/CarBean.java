package iiml.wmp.ibfs.car;

import java.util.*;

import iiml.wmp.ibfs.beans.ScreenerBean;

public class CarBean {
    //Here the assumption is that we have 21 pre event days data 1 event day data and 20 post event days data
    //Estimation data size can vary
    //Screener bean for other things like company name balancesheets etc


    ScreenerBean screenerBean;

    Map<Integer,Double> announcementDateEvent;
    Map<Integer,Double> announcementDateEstimation;

    Map<Integer,Double> recordDateEvent;
    Map<Integer,Double> recordDateEstimation;

    Map<Integer,Double> exrightsDateEvent;
    Map<Integer,Double> exrightsDateEstimation;

    Double announcementEstimationAverageReturn;
    Double announcementEventAverageReturn;

    Double recordEstimationAverageReturn;
    Double recordEventAverageReturn;

    Double exRightsEstimationAverageReturn;
    Double exRightsEventAverageReturn;

    Map<Integer,Double> ABannouncementDateEvent;
    Map<Integer,Double> ABannouncementDateEstimation;

    Map<Integer,Double> ABrecordDateEvent;
    Map<Integer,Double> ABrecordDateEstimation;

    Map<Integer,Double> ABexrightsDateEvent;
    Map<Integer,Double> ABexrightsDateEstimation;


    public CarBean(ScreenerBean screenerBean,
                   Map<Integer, Double> announcementDateEvent,
                   Map<Integer, Double> announcementDateEstimation,
                   Map<Integer, Double> recordDateEvent,
                   Map<Integer, Double> recordDateEstimation,
                   Map<Integer, Double> exrightsDateEvent,
                   Map<Integer, Double> exrightsDateEstimation) {
        this.screenerBean = screenerBean;
        this.announcementDateEvent = announcementDateEvent;
        this.announcementDateEstimation = announcementDateEstimation;
        this.recordDateEvent = recordDateEvent;
        this.recordDateEstimation = recordDateEstimation;
        this.exrightsDateEvent = exrightsDateEvent;
        this.exrightsDateEstimation = exrightsDateEstimation;
        this.announcementEstimationAverageReturn = getAverageReturn(announcementDateEstimation);
        this.announcementEventAverageReturn = getAverageReturn(announcementDateEvent);
        this.recordEstimationAverageReturn = getAverageReturn(recordDateEstimation);
        this.recordEventAverageReturn = getAverageReturn(recordDateEvent);
        this.exRightsEstimationAverageReturn = getAverageReturn(exrightsDateEstimation);
        this.exRightsEventAverageReturn = getAverageReturn(exrightsDateEvent);
        this.ABannouncementDateEstimation = getAbnormalReturn(announcementDateEstimation,announcementEstimationAverageReturn);
        this.ABannouncementDateEvent = getAbnormalReturn(announcementDateEvent,announcementEventAverageReturn);
        this.ABexrightsDateEstimation = getAbnormalReturn(exrightsDateEstimation,exRightsEstimationAverageReturn);
        this.ABexrightsDateEvent = getAbnormalReturn(exrightsDateEvent,exRightsEventAverageReturn);
        this.ABrecordDateEstimation = getAbnormalReturn(recordDateEstimation,recordEstimationAverageReturn);
        this.ABexrightsDateEvent =getAbnormalReturn(recordDateEvent,recordEventAverageReturn);


    }

    private Double getAverageReturn(Map<Integer,Double> values){
        boolean first = true;
        Double cumReturn = 0.0;
        for(Integer ind:values.keySet()){
            if(first){
                first = false;
            }
            else{
                Double cur = values.get(ind);
                Double prev = values.get(ind-1);
                cumReturn += (cur-prev)/prev;
            }
        }
        Double averageReturn = cumReturn/(values.keySet().size()-1);
        return averageReturn;
    }

    private Map<Integer,Double> getAbnormalReturn(Map<Integer,Double> values,Double averageReturn){
        boolean first = true;
        Double abnormalReturn = 0.0;
        Map<Integer,Double> abnormalReturnMap = new TreeMap<>();
        for(Integer ind:values.keySet()){
            if(first){
                first = false;
            }
            else{
                Double cur = values.get(ind);
                Double prev = values.get(ind-1);
                abnormalReturn = ((cur-prev)/prev)-averageReturn;
                abnormalReturnMap.put(ind,abnormalReturn);
            }
        }
        return  abnormalReturnMap;
    }
}

