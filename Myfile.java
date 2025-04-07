    // uid & stdid processing
    if(!adRecords.isEmpty()) {
    logger.info("**********AD Service Records Processing**********");
    
    Map<String, String> uidMap = ADConnect.getStdIdFromADServices(adRecordsSubject, 5, "stdid");
    
    for(SSOSplunkEvent eventRec : adRecords) {
        String stdId = uidMap.get(eventRec.getSubject().toUpperCase());
        
        if(isNullOrEmpty(stdId)) {
            eventRec.setStdId(stdId);
            ssoEventData.add(eventRec);
        } else {
            excludedCount++;
            adExcludedRecords.add(eventRec.getSubject().toUpperCase());
            logger.debug("UtilInfo: Std Id not found in CDSN : " + eventRec.toString(Boolean.TRUE));
        }
    }
    }

       // pno processing
       if(!adPnoRecords.isEmpty()) {
        logger.info("**********AD Service Records Processing**********");
        
        Map<String, String> uidMap = ADConnect.getStdIdFromADServices(adPnoRecordsSubject, 5, "pno");
        
        for(SSOSplunkEvent eventRec : adPnoRecords) {
            String stdId = uidMap.get(eventRec.getSubject().toUpperCase());
            
            if(isNullOrEmpty(stdId)) {
                eventRec.setStdId(stdId);
                ssoEventData.add(eventRec);
            } else {
                excludedCount++;
                adExcludedRecords.add(eventRec.getSubject().toUpperCase());
                logger.debug("UtilInfo: Std Id not found in CDSN : " + eventRec.toString(Boolean.TRUE));
            }
        }
    }

       // email processing
       if(!adEmailRecords.isEmpty()) {
        logger.info("**********AD Service Records Processing**********");
        
        Map<String, String> uidMap = ADConnect.getStdIdFromADServices(adEmailRecordsSubject, 5, "email");
        
        for(SSOSplunkEvent eventRec : adEmailRecords) {
            String stdId = uidMap.get(eventRec.getSubject().toUpperCase());
            
            if(isNullOrEmpty(stdId)) {
                eventRec.setStdId(stdId);
                ssoEventData.add(eventRec);
            } else {
                excludedCount++;
                adExcludedRecords.add(eventRec.getSubject().toUpperCase());
                logger.debug("UtilInfo: Std Id not found in CDSN : " + eventRec.toString(Boolean.TRUE));
            }
        }
    }
