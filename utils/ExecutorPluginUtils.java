package com.yiyouliao.autoprod.liaoyuan.utils;

import com.yiyouliao.autoprod.liaoyuan.annotation.ProdJobTag;
import com.yiyouliao.autoprod.liaoyuan.annotation.ProdJobTagIgnore;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * TODO
 *
 * @author tengwang8
 * @version 1.0
 * @date 2022/3/2 9:07
 */
public class ExecutorPluginUtils {

    /**
     * 获取sql语句
     *
     * @param invocation
     * @return
     */
    public static String getSqlByInvocation(Invocation invocation) {
        final Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameterObject = args[1];
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        return boundSql.getSql();
    }

    /**
     * 包装sql后，重置到invocation中
     *
     * @param invocation
     * @param sql
     * @throws SQLException
     */
    public static void resetSql2Invocation(Invocation invocation, String sql) throws SQLException {
        final Object[] args = invocation.getArgs();
        MappedStatement statement = (MappedStatement) args[0];
        Object parameterObject = args[1];
        BoundSql boundSql = statement.getBoundSql(parameterObject);
        MappedStatement newStatement = newMappedStatement(statement, new BoundSqlSqlSource(boundSql));
        MetaObject msObject = MetaObject.forObject(newStatement, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        msObject.setValue("sqlSource.boundSql.sql", sql);
        args[0] = newStatement;
    }


    private static MappedStatement newMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder =
                new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    /**
     * 是否标记为区域字段
     *
     * @return
     */
    public static boolean isAreaTag(MappedStatement mappedStatement) throws ClassNotFoundException {
        String id = mappedStatement.getId();
        Class<?> classType = Class.forName(id.substring(0, mappedStatement.getId().lastIndexOf(".")));

        //获取对应拦截方法名
        String mName = mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf(".") + 1);

        boolean ignore = false;

        for (Method method : classType.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ProdJobTagIgnore.class) && mName.equals(method.getName())) {
                ignore = true;
            }
        }

        if (classType.isAnnotationPresent(ProdJobTag.class) && !ignore) {
            return true;
        }
        return false;
    }


    /**
     * 是否标记为区域字段
     *
     * @return
     */
    public static boolean isAreaTagIngore(MappedStatement mappedStatement) throws ClassNotFoundException {
        String id = mappedStatement.getId();
        Class<?> classType = Class.forName(id.substring(0, mappedStatement.getId().lastIndexOf(".")));
        //获取对应拦截方法名
        String mName = mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf(".") + 1);
        boolean ignore = false;
        for (Method method : classType.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ProdJobTagIgnore.class) && mName.equals(method.getName())) {
                ignore = true;
            }
        }
        return ignore;
    }


    public static String getOperateType(Invocation invocation) {
        final Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        SqlCommandType commondType = ms.getSqlCommandType();
        if (commondType.compareTo(SqlCommandType.SELECT) == 0) {
            return "select";
        }
        if (commondType.compareTo(SqlCommandType.INSERT) == 0) {
            return "insert";
        }
        if (commondType.compareTo(SqlCommandType.UPDATE) == 0) {
            return "update";
        }
        if (commondType.compareTo(SqlCommandType.DELETE) == 0) {
            return "delete";
        }
        return null;
    }

    //定义一个内部辅助类，作用是包装sql
    static class BoundSqlSqlSource implements SqlSource {
        private final BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }


}


