package com.metadata.adapter.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.metadata.adapter.api.IntegrationLibraryService;
import com.metadata.common.IhubPropertyConstants;
import com.metadata.config.IntegrationConfigProperties;
import com.metadata.rest.dto.Expression;
import com.metadata.rest.dto.NestedExpressionBean;
import com.metadata.rest.dto.QueryFilter;
import com.metadata.rest.dto.QueryIPackRequest;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class IntegrationLibraryServiceImpl implements IntegrationLibraryService {

    public static final ObjectMapper mapper = new ObjectMapper();

    static IntegrationConfigProperties properties;

    public IntegrationLibraryServiceImpl(IntegrationConfigProperties properties) {
        this.properties = properties;
    }

    /**
     * @param argument will be a list of arguments passed
     * @param property will be the name of property in the expression
     * @param operator will be the operator in Queryfilter
     * @return QueryBoomiIPackRequest
     */
    public QueryIPackRequest createSimpleQueryExpression(List<String> argument,
                                                         String property, String operator) {
        QueryIPackRequest qba = new QueryIPackRequest();
        QueryFilter qf = new QueryFilter();
        Expression expression = new Expression();
        expression.setArgument(argument);
        expression.setOperator(operator);
        expression.setProperty(property);
        qf.setExpression(expression);
        qba.setQueryFilter(qf);
        return qba;
    }

    public HttpHeaders getHttpHeader() {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, "Basic YWpheS5zaW5naEB1a2cuQ09NOkluZGlhIzEyMzQzMjE=");
        return headers;
    }

    public List<NestedExpressionBean> createNestedExpression(
            String firstParam, String secondParam, String firstParamName, String secondParamName) {
        List<NestedExpressionBean> nestedBean = new ArrayList<>();
        NestedExpressionBean accountGroupIDBean = new NestedExpressionBean();
        NestedExpressionBean accountIdBean = new NestedExpressionBean();
        accountGroupIDBean.setArgument(Arrays.asList(firstParam));
        accountGroupIDBean.setOperator(IhubPropertyConstants.BoomiOpretorEnum.EQUALS.toString());
        accountGroupIDBean.setProperty(firstParamName);
        accountIdBean.setArgument(Arrays.asList(secondParam));
        accountIdBean.setOperator(IhubPropertyConstants.BoomiOpretorEnum.EQUALS.toString());
        accountIdBean.setProperty(secondParamName);
        nestedBean.add(accountGroupIDBean);
        nestedBean.add(accountIdBean);
        return nestedBean;

    }

    public QueryIPackRequest createNestedQueryExpression(String operator, String firstParam, String secondParam,
                                                         String firstParamName, String secondParamName) {
        QueryIPackRequest qba = new QueryIPackRequest();
        QueryFilter queryFilter = new QueryFilter();
        Expression expression = new Expression();
        List<NestedExpressionBean> expressionBeanList = createNestedExpression(firstParam, secondParam,
                firstParamName, secondParamName);
        expression.setOperator(operator);
        expression.setNestedExpression(expressionBeanList);
        queryFilter.setExpression(expression);
        qba.setQueryFilter(queryFilter);
        return qba;

    }

    public URI getUri(String apiUrl, String overrideAccountID) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(properties.getBaseUrl())
                .path(IhubPropertyConstants.PRIMARY_ACCOUNT_ID)
                .path(apiUrl)
                .queryParam(IhubPropertyConstants.OVERRIDE_ACCOUNT, overrideAccountID)
                .build();
        return uriComponents.toUri();
    }

    public URI getAccountUri(String apiUrl) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(properties.getBaseUrl())
                .path(IhubPropertyConstants.PRIMARY_ACCOUNT_ID)
                .path(apiUrl)
                .build();
        return uriComponents.toUri();
    }
}
