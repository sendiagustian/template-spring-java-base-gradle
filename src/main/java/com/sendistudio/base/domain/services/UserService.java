package com.sendistudio.base.domain.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.sendistudio.base.app.utils.ErrorUtil;
import com.sendistudio.base.data.responses.DataResponse;
import com.sendistudio.base.data.responses.WebResponse;
import com.sendistudio.base.domain.repositories.SampleRepo;

@Service
public class UserService {
    @Autowired
    private ErrorUtil errorHandler;

    @Autowired
    private SampleRepo repository;

    public WebResponse getAll() {
        try {
            List<String> data = repository.getAll();
            DataResponse<List<String>> response = new DataResponse<>();

            response.setStatus(true);
            response.setData(data);

            return response;
        } catch (DataAccessException e) {
            return errorHandler.errorNotFound(e);
        } catch (Exception e) {
            return errorHandler.errorServer(e);
        }
    }
}
