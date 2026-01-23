when(jdbcTemplate.update(
        anyString(),
        ArgumentMatchers.<Object[]>any()
)).thenReturn(1);


verify(jdbcTemplate, times(1)).update(
        anyString(),
        ArgumentMatchers.<Object[]>any()
);
