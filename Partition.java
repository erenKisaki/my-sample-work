when(environment.getProperty(anyString(), eq(Boolean.class), anyBoolean()))
        .thenAnswer(invocation -> invocation.getArgument(2));
