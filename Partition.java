when(jdbcTemplate.update(
        anyString(),
        ArgumentMatchers.<Object[]>any()
)).thenReturn(1);


verify(jdbcTemplate, times(1)).update(
        anyString(),
        ArgumentMatchers.<Object[]>any()
);

@Test
void shouldKeepAllWhenAllMatch() {
    BatchPaymentWorkListUtil.batchReauthCustomExceptionList = Arrays.asList(
        new BatchReauthCustomException(5L),
        new BatchReauthCustomException(5L)
    );

    BatchPaymentWorkListUtil.updateBatchReauthCustomExceptionListByReauthRule(5L);

    assertEquals(
        2,
        BatchPaymentWorkListUtil.batchReauthCustomExceptionList.size()
    );
}
