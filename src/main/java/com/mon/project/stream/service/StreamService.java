package com.mon.project.stream.service;

import com.mon.project.stream.vo.SpartVO;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamService {


    //无rowId的分组
    public void partitionSparts(){
        List<SpartVO> list = getSpart();
        Stream<SpartVO> listStrean = list.parallelStream();
        //根据resSalesType分组
        Map<Boolean,List<SpartVO>> partitionList = list.parallelStream().collect(
                Collectors.partitioningBy( l -> "device".equals( l.getResSalesType() ))
        );
        List<SpartVO> deviceList = partitionList.get(true);
        List<SpartVO> serviceList = partitionList.get(false);
        System.out.println("partitionBydevice : "+deviceList.toString());
        if( !serviceList.isEmpty() ){
            Map<String,List<SpartVO>> mTONPart = serviceList.parallelStream().collect(
                    Collectors.groupingByConcurrent(d -> identificatMToN(d) )
            );
            System.out.println("mTONPart : "+mTONPart.toString());
            Map<String,Map<String,List<SpartVO>>> rowSpart = rowSpart(mTONPart);
            rowSpart.entrySet().forEach( d ->
                    System.out.println("each father key : "+d.getKey()+" value : "+d.getValue())
            );



        }
    }

    public Map<String,Map<String,List<SpartVO>>> rowSpart(Map<String,List<SpartVO>> sparts){
        Map<String,Map<String,List<SpartVO>>> rowSpart = new HashMap<>();
        Map<String,List<SpartVO>> M_TO_N = sparts.get("M:N").parallelStream().collect(
                Collectors.groupingByConcurrent(SpartVO::getTgtBoqId)
        );
        rowSpart.put("M:N",M_TO_N);
        Map<String,List<SpartVO>> ONE_TO_ONE = sparts.get("1:1").parallelStream().collect(
                Collectors.groupingByConcurrent(SpartVO::getTgtBoqId)
        );
        rowSpart.put("1:1",ONE_TO_ONE);
        Map<String,List<SpartVO>> M_TO_ONE = sparts.get("M:1").parallelStream().collect(
                Collectors.groupingByConcurrent(SpartVO::getTgtBoqId)
        );
        rowSpart.put("M:1",M_TO_ONE);
        Map<String,List<SpartVO>> ONE_TO_N = sparts.get("1:N").parallelStream().collect(
                Collectors.groupingByConcurrent(SpartVO::getResBoqId)
        );
        rowSpart.put("1:N",ONE_TO_N);
        return rowSpart;
    }


    public String identificatMToN(SpartVO SpartVO){
        if( SpartVO.getResQuantity() > 1 &&  SpartVO.getTgtQuantity() > 1 ){
            return "M:N";
        }else if (SpartVO.getResQuantity() == 1 && SpartVO.getTgtQuantity() == 1 ){
            return "1:1";
        }else if (SpartVO.getResQuantity() > 1  && SpartVO.getTgtQuantity() == 1){
            return "M:1";
        }else if (SpartVO.getResQuantity() == 1 && SpartVO.getTgtQuantity() > 1) {
            return "1:N";
        }else{
            return "ResouceMatch";
        }
    }

    //有rowId的分组
    public void partitionSpart(){
        List<SpartVO> list = getSpart();
        Stream<SpartVO> listStrean = list.parallelStream();

        //根据resSalesType分组
        Map<Boolean,List<SpartVO>> partitionList = list.parallelStream().collect(
                Collectors.partitioningBy( l -> "device".equals( l.getResSalesType() ))
        );
        List<SpartVO> deviceList = partitionList.get(true);
        List<SpartVO> serviceList = partitionList.get(false);
        System.out.println("partitionBydevice : "+deviceList.toString());

        //根据rowId分组数据
        ConcurrentMap<String, List<SpartVO>> partitionByRowId0 = serviceList.parallelStream().collect(
                Collectors.groupingByConcurrent(SpartVO::getRowId)
        );
        System.out.println("partitionByRowId0 : "+partitionByRowId0.toString());
        //以分组后的数据判定1:1 1:N ..类型
        Map<String, List<Map.Entry<String, List<SpartVO>>>> identificatMap = partitionByRowId0.entrySet().parallelStream().collect(
                Collectors.groupingByConcurrent( l -> identificatMToN( l.getValue()))
        );
        System.out.println("partitionByRowId0 entrySet : "+identificatMap.toString());
        //根据rowId分组数据
        ConcurrentMap<Integer, Set<String>> partitionByRowId = deviceList.parallelStream().collect(
                Collectors.groupingByConcurrent(SpartVO::getTgtQuantity,
                        Collectors.mapping(SpartVO::getRowId,Collectors.toSet()))
        );
        System.out.println("partitionByRowId : "+partitionByRowId.toString());
    }
    public static String identificatMToN(List<SpartVO> rowSparts){
        //System.out.println("rowSparts : "+rowSparts.toString() );
        if( rowSparts.parallelStream().anyMatch(w -> w.getResQuantity() > 1 && w.getTgtQuantity() > 1 )){
            return "M:N";
        }else if (rowSparts.parallelStream().allMatch(w -> w.getResQuantity() == 1 && w.getTgtQuantity() == 1 )){
            return "1:1";
        }else if (rowSparts.parallelStream().allMatch(w -> w.getResQuantity() > 1  && w.getTgtQuantity() == 1)){
            return "M:1";
        }else if (rowSparts.parallelStream().allMatch(w -> w.getResQuantity() == 1  && w.getTgtQuantity() > 1)) {
            return "1:N";
        }else{
            return "1:1";
        }
    }


    public List<SpartVO> getSpart(){
        List<SpartVO> list = new ArrayList<>();
        //M:1服务  预销 核销
        list.add(new SpartVO("1","1111","1222","service",1,3));
        list.add(new SpartVO("1","1111","1333","service",1,3));
        list.add(new SpartVO("1","1111","1444","service",1,3));
        //M:1设备  预销 核销
        list.add(new SpartVO("2","2111","2222","device",1,3));
        list.add(new SpartVO("2","2111","2333","device",1,3));
        list.add(new SpartVO("2","2111","2444","device",1,3));
        //1:N服务 预销 核销
        list.add(new SpartVO("3","3222","3111","service",3,1));
        list.add(new SpartVO("3","3333","3111","service",3,1));
        list.add(new SpartVO("3","3444","3111","service",3,1));
        //1:N设备  预销 核销
        list.add(new SpartVO("4","4222","4111","device",3,1));
        list.add(new SpartVO("4","4333","4111","device",3,1));
        list.add(new SpartVO("4","4444","4111","device",3,1));
        //1:1服务  预销 核销
        list.add(new SpartVO("5","5222","5111","service",1,1));
        list.add(new SpartVO("5","5333","5112","service",1,1));
        list.add(new SpartVO("5","5444","5113","service",1,1));
        //1:1设备  预销 核销
        list.add(new SpartVO("6","6222","6111","device",1,1));
        list.add(new SpartVO("6","6333","6112","device",1,1));
        list.add(new SpartVO("6","6444","6113","device",1,1));
        //N:M服务  预销 核销
        list.add(new SpartVO("7","7111","7222","service",2,3));
        list.add(new SpartVO("7","7111","7333","service",2,3));
        list.add(new SpartVO("7","7111","7444","service",1,3));
        list.add(new SpartVO("7","7322","7222","service",2,1));
        list.add(new SpartVO("7","7333","7112","service",1,1));
        list.add(new SpartVO("7","7444","7333","service",2,2));
        list.add(new SpartVO("7","7444","7335","service",1,2));
        list.add(new SpartVO("7","75555","75555","device",1,1));

        //1:0
        list.add(new SpartVO("8","8444","","service",1,0));
        return list;
    }




}
