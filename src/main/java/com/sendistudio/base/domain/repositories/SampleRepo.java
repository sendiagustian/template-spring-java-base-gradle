package com.sendistudio.base.domain.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sendistudio.base.app.utils.QueryUtil;
import com.sendistudio.base.app.utils.TypeUtil;

@Repository
public class SampleRepo {
    @Autowired
    private QueryUtil query;

    public List<String> getAll() {
        String sql = "SELECT * FROM tbUsers";
        return query.queryForList(sql, new TypeUtil.StringRowMapper("username"));
    }

}
