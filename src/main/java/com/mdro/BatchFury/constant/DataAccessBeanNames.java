package com.mdro.BatchFury.constant;

public final class DataAccessBeanNames {
    private DataAccessBeanNames() {}

    public static final class DataSource {
        private DataSource() {}
        public static final String METADATA = "dataSource";
        public static final String LOCAL = "localDataSource";
        public static final String REMOTE = "remoteDataSource";
    }

    public static final class JdbcTemplate {
        private JdbcTemplate() {}
        public static final String METADATA = "metadataJdbcTemplate";
        public static final String LOCAL = "localJdbcTemplate";
        public static final String REMOTE = "remoteJdbcTemplate";
    }

    public static final class TransactionManager {
        private TransactionManager() {}
        public static final String METADATA = "transactionManager";
        public static final String LOCAL = "localTransactionManager";
        public static final String REMOTE = "remoteTransactionManager";
    }
}
