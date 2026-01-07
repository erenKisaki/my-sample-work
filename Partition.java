 public LocalDateTime getEligibleNextRun(LocalDateTime currentDate) {

        boolean isEligibleRunFound = false;
        LocalDateTime runDate = null;

        while (!isEligibleRunFound) {

            runDate = getNextRun(runDate, currentDate);

            if (isExceptionQualified(runDate)) {
                isEligibleRunFound = true;
            }
        }

        return runDate;
    }
