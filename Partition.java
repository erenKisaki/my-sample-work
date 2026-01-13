@ExtendWith(MockitoExtension.class)
class BatchReauthCustomExceptionDaoImplTest {

    @InjectMocks
    private BatchReauthCustomExceptionDaoImpl dao;

    @Mock
    private PaymentDatabaseConnector paymentDatabaseConnector;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private QueryBuilder<BatchReauthCustomException> queryBuilder;

    @BeforeEach
    void setup() {
        when(paymentDatabaseConnector.getJdbcTemplate()).thenReturn(jdbcTemplate);
    }

    @Test
    void get_ShouldReturnList_WhenDataExists() {
        BatchReauthCustomException record = new BatchReauthCustomException();
        List<BatchReauthCustomException> expectedList = List.of(record);

        when(jdbcTemplate.query(anyString(), any(ResultSetExtractor.class)))
                .thenReturn(expectedList);

        List<BatchReauthCustomException> result = dao.get();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void get_ShouldReturnNull_WhenEmptyList() {
        when(jdbcTemplate.query(anyString(), any(ResultSetExtractor.class)))
                .thenReturn(Collections.emptyList());

        List<BatchReauthCustomException> result = dao.get();

        assertNull(result);
    }

    @Test
    void getExtractor_ShouldMapResultSetCorrectly() throws Exception {
        ResultSet rs = mock(ResultSet.class);

        when(rs.next()).thenReturn(true, false);
        when(rs.getString("DAY")).thenReturn("MONDAY");
        when(rs.getString("EXCLUDE_ERROR")).thenReturn("ERR01");
        when(rs.getInt("DAY_OF_THE_MONTH")).thenReturn(10);
        when(rs.getString("INCLUDE_ERROR")).thenReturn("ERR02");
        when(rs.getLong("TIME")).thenReturn(1000L);
        when(rs.getInt("REAUTH_RULE")).thenReturn(1);

        ResultSetExtractor<List<BatchReauthCustomException>> extractor = dao.getExtractor();

        List<BatchReauthCustomException> result = extractor.extractData(rs);

        assertEquals(1, result.size());
        assertEquals("MONDAY", result.get(0).getDay());
    }
}
