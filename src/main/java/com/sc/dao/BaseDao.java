package com.sc.dao;


import com.sc.entity.BaseDTO;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*import org.apache.commons.lang3.ArrayUtils;*/


@Component

public class BaseDao {
    Log log = LogFactory.getLog(BaseDao.class);
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    //新增
    public <T extends BaseDTO> Integer insert(String sql, T t){
        try {
            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(t);
            namedParameterJdbcTemplate.update(sql, namedParameters,keyholder);
            Integer pk = keyholder.getKey().intValue();
            return pk;
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }
    }

    //新增2
    public <T extends BaseDTO> Integer insert2(T t){
        try {
            String sql = makeSql4Insert(t.getClass().getSimpleName());
            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(t);
            namedParameterJdbcTemplate.update(sql, namedParameters,keyholder);
            Integer pk = keyholder.getKey().intValue();
            return pk;
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }
    }

    //修改
    public <T extends BaseDTO> int update(String sql,T t){
        try {
            SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(t);
            namedParameterJdbcTemplate.update(sql, namedParameters);
            return 0;
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }
    }

    //修改2
    public <T extends BaseDTO> int update2(T t){
        try {
            String sql = makeSql4Update(t.getClass().getSimpleName());
            SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(t);
            namedParameterJdbcTemplate.update(sql, namedParameters);
            return 0;
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }
    }

    public int update2(String sql,Object[] params){
        try {
            jdbcTemplate.update(sql, params);
            return 0;
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }
    }
    //删除
    public int delete(String sql,Object...ids){
        try {
            return jdbcTemplate.update(sql,ids);
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }
    }

    //删除，提供表名和id即可
    public int deleteById(String table,Object id){
        final StringBuilder sql = new StringBuilder();
        sql.append("delete from ");
        sql.append(table);
        sql.append(" where ").append(getPKColNameByTableName(table)).append(" = ?");
        try {
            return jdbcTemplate.update(sql.toString(),id);
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }
    }

    //批量写
    public <T extends BaseDTO> void batch(final String sql,List<T> t){
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(t.toArray());
        namedParameterJdbcTemplate.batchUpdate(sql, batch);
    }

    public BaseDao() {
        super();
    }

    //查询
    public <T extends BaseDTO> List<T> query(String sql, Class<T> beanClass, Object[] params) {
        return jdbcTemplate.query(sql,params,new BeanPropertyRowMapper<T>(beanClass));
    }

    public <T extends BaseDTO> List<T> query(String sql, Class<T> beanClass) {
        return jdbcTemplate.query(sql,new BeanPropertyRowMapper<T>(beanClass));
    }

    //分页查询
    public <T extends BaseDTO> List<T> queryBySlice(String sql, Class<T> beanClass, Object[] params,int page, int count) {
        if(page <= 0 || count <= 0){ // find all
            return query(sql, beanClass,params);
        }else{
            int from = (page - 1) * count;
            count = (count > 0) ? count : Integer.MAX_VALUE;
            Pattern p = Pattern.compile("order\\s*by.*");
            Matcher m = p.matcher(sql);
            String sqlpart="";
            if(m.find()){
                sqlpart="("+ sql.substring(0,sql.indexOf(m.group(0)))+") xx";
            }else{
                sqlpart="("+ sql.substring(0,sql.length())+") xx";
            }
            String sqlcount="select count(*) total  from ";
            sqlcount=sqlcount+sqlpart;
            List<BaseDTO> totalList=query(sqlcount, BaseDTO.class, params);

            List<T> result=query(sql + " LIMIT ?,?",beanClass, ArrayUtils.addAll(params, new Object[]{from, count}));
            for(T t:result){
                t.setTotal(totalList.get(0).getTotal());
            }
            return result;
        }
    }

    //查询byID
    public <T extends BaseDTO> T queryById(String sql, Class<T> beanClass, Object... ids) {
        try {
            return jdbcTemplate.queryForObject(sql,ids,new BeanPropertyRowMapper<T>(beanClass));
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    //查询返回Object
    public <T extends Object> T queryForObject(String sql, Class<T> beanClass, Object[] params) {
        try {
            return jdbcTemplate.queryForObject(sql,params,beanClass);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    //校验值是否存在
    public int chkIsExist(String table,String col,String colVal){
        final StringBuilder sql = new StringBuilder();
        sql.append(" select count(*) as c from ");
        sql.append(table);
        sql.append(" where ").append(col).append(" = ").append("'" + colVal + "'");
        return jdbcTemplate.queryForObject(sql.toString(), Integer.class);
    }

    //根据数据库名及表名获得所有字段
    public List<String> getTableColumns(String tableName){
        List<String> list = new ArrayList<String>();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            stat = conn.createStatement();
            rs = stat.executeQuery("select * from "+tableName);
            ResultSetMetaData rss = rs.getMetaData();
            int columnCount = rss.getColumnCount();
            for(int i = 1;i <= columnCount; i++){
                list.add(rss.getColumnName(i));
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }finally {
            jdbcClose(conn, stat, rs);
        }
        return list;
    }


    public List<Integer> getTableColumnTypes(String tableName){
        List<Integer> list = new ArrayList<Integer>();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            stat = conn.createStatement();
            rs = stat.executeQuery("select * from "+tableName);
            ResultSetMetaData rss = rs.getMetaData();
            int columnCount = rss.getColumnCount();
            for(int i = 1;i <= columnCount; i++){
                list.add(rss.getColumnType(i));
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }finally {
            jdbcClose(conn, stat, rs);
        }
        return list;
    }

    //根据表名获取主键字段名称
    public String getPKColNameByTableName(String tableName){
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            stat = conn.createStatement();
            rs = stat.executeQuery("select * from "+tableName);
            ResultSetMetaData rss = rs.getMetaData();
            return rss.getColumnName(1);
        } catch (SQLException e) {
            log.error(e.getMessage());
            return null;
        }finally {
            jdbcClose(conn, stat, rs);
        }
    }


    //根据表名生成insert sql
    public String makeSql4Insert(String table){
        List<String> cols = getTableColumns(table);
        final StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(table);
        sql.append("(");
        StringBuilder colStr = new StringBuilder();
        for(int i=1;i<cols.size();i++){
            colStr.append(cols.get(i)).append(",");
        }
        sql.append(colStr.substring(0,colStr.length()-1));
        sql.append(")");
        sql.append(" values( ");
        StringBuilder colStr2 = new StringBuilder();
        for(int i=1;i<cols.size();i++){
            String colVal = cols.get(i);
            if(colVal != null && colVal.equalsIgnoreCase("cdate")){//处理插入时间，直接获取系统时间
                colStr2.append("now()").append(",");
            }else{
                colStr2.append(":").append(cols.get(i)).append(",");
            }
        }
        sql.append(colStr2.substring(0,colStr2.length()-1));
        sql.append(" ) ");
        return sql.toString();
    }

    //根据表名生成update sql
    public String makeSql4Update(String table){
        List<String> cols = getTableColumns(table);
        final StringBuilder sql = new StringBuilder();
        sql.append("update ");
        sql.append(table);
        sql.append(" set ");
        StringBuilder s = new StringBuilder();
        for(int i=1;i<cols.size();i++){
            String colName = cols.get(i);
            s.append(colName).append(" = ").append(":").append(colName).append(",");
        }
        sql.append(s.substring(0,s.length()-1));
        sql.append(" where ").append(cols.get(0)).append(" = ").append(":").append(cols.get(0));
        return sql.toString();
    }

    //根据表名生成select all sql
    public String makeSql4SelectAll(String table){
        return "select * from " + table;
    }

    //根据表名生成select by id sql
    public String makeSql4SelectById(String table){
        String pk = getPKColNameByTableName(table);
        return "select * from " + table + " where " + pk + " = ?";
    }
    private void jdbcClose(Connection conn, Statement stat, ResultSet rs) {
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(stat != null){
            try {
                stat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //查询返回Object
    public int queryForInt(String sql, Object[] params) {
        try {
            Integer k = jdbcTemplate.queryForObject(sql,params,Integer.class);
            if(k==null){
                return 0;
            }else {
                return k.intValue();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return 0;
        }
    }

    //新增2
    public <T extends BaseDTO> Integer insert3(T t){
        try {
            String sql = makeSql4Insert("t_"+t.getClass().getSimpleName());
            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(t);
            namedParameterJdbcTemplate.update(sql, namedParameters,keyholder);
            Integer pk = keyholder.getKey().intValue();
            return pk;
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }
    }

}
