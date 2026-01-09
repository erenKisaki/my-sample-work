List<BatchRetryCustomException> mockExceptions = new ArrayList<>();

MockedStatic<BatchPaymentWorkListUtil> mocked =
                 mockStatic(BatchPaymentWorkListUtil.class);

 mocked.when(BatchPaymentWorkListUtil::getBatchRetryCustomExceptionList)
              .thenReturn(mockExceptions);
