@ExtendWith(MockitoExtension.class)
class PaymentProcessingHistoryDaoImplTest {

    @InjectMocks
    private PaymentProcessingHistoryDaoImpl dao;

    @Mock
    private PaymentDatabaseConnector paymentDatabaseConnector;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        when(paymentDatabaseConnector.getJdbcTemplate()).thenReturn(jdbcTemplate);
    }

    @Test
    void add_ShouldInsertSuccessfully() {
        PaymentProcessingHistory history = new PaymentProcessingHistory();

        PaymentProcessingHistoryDaoImpl spyDao = spy(dao);
        doReturn(100L).when(spyDao).getNextSeqNumber(anyString());
        doReturn(1).when(jdbcTemplate).update(anyString(), any());

        Long result = spyDao.add(history);

        assertNotNull(result);
        assertEquals(100L, result);
    }

    @Test
    void add_ShouldThrowException_WhenSequenceNull() {
        PaymentProcessingHistory history = new PaymentProcessingHistory();

        PaymentProcessingHistoryDaoImpl spyDao = spy(dao);
        doReturn(null).when(spyDao).getNextSeqNumber(anyString());

        assertThrows(RuntimeException.class, () -> spyDao.add(history));
    }

    @Test
    void insert_ShouldReturnSequence_WhenInsertSuccess() {
        PaymentProcessingHistory history = new PaymentProcessingHistory();

        when(jdbcTemplate.update(anyString(), any())).thenReturn(1);

        Long result = dao.insert(history, 10L);

        assertEquals(10L, result);
    }

    @Test
    void insert_ShouldReturnNull_WhenInsertFails() {
        PaymentProcessingHistory history = new PaymentProcessingHistory();

        when(jdbcTemplate.update(anyString(), any())).thenReturn(0);

        Long result = dao.insert(history, 10L);

        assertNull(result);
    }
}
