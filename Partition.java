
@RunWith(PowerMockRunner.class)
@PrepareForTest(BatchPaymentWorkListUtil.class);

List<BatchRetryCustomException> mockExceptions = new ArrayList<>();

PowerMockito.mockStatic(BatchPaymentWorkListUtil.class);

PowerMockito.when(BatchPaymentWorkListUtil.getBatchRetryCustomExceptionList())
            .thenReturn(mockExceptions);
