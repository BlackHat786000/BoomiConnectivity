package com.metadata.adapter.impl;

import com.metadata.adapter.api.BoomiVerificationService;
import com.metadata.adapter.api.IntegrationLibraryService;
import com.metadata.adapter.api.TPRetryBoomiService;
import com.metadata.common.ErrorMessagePropertyConstants;
import com.metadata.common.IhubPropertyConstants;
import com.metadata.config.BoomiConfig;
import com.metadata.exception.TPApiServiceException;
import com.metadata.rest.dto.*;
import com.metadata.rest.dto.envext.CrossReference;
import com.metadata.rest.dto.envext.EnvironmentExtension;
import com.metadata.rest.dto.envext.ProcessProperty;
import com.metadata.rest.dto.envext.ProcessPropertyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BoomiVerificationServiceImpl implements BoomiVerificationService {

    @Autowired
    private BoomiConfig boomiInformation;

    private final TPRetryBoomiService tpRetryBoomiService;

    private final IntegrationLibraryService integrationLibraryService;

//    private final IntegrationLibraryUtil util;

    private final Logger logger = LoggerFactory.getLogger(BoomiVerificationServiceImpl.class);

    public BoomiVerificationServiceImpl(TPRetryBoomiService tpRetryBoomiService,
                                        IntegrationLibraryService integrationLibraryService) {
//        this.boomiInformation = boomiInformation;
        this.tpRetryBoomiService = tpRetryBoomiService;
//        this.util = util;
        this.integrationLibraryService = integrationLibraryService;
    }

    public ResponseObject verifyBoomiConnectivity(RequestObject request){

//        request.setIPackName(request.getIPackName());
        // queryIpackInstanceID(accID,ipackName)
        String integrationPackInstanceId = queryIntegrationPackInstanceId(request.getIPackName(), boomiInformation.getAccountId());

        if(integrationPackInstanceId!=null)
        {
            ResponseObject response = new ResponseObject();
            //getProcessDetail
            ProcessDetails processDetail = getProcessDetail(request,boomiInformation.getAccountId());
            response.setProcessDetails(processDetail);

            //getCrtDetails(accId,envId,processName)
            List<String> crtDetail = getCrtDetails(boomiInformation.getEnvironmentID(),boomiInformation.getAccountId(), request.getProcessName());
            response.setCrtDetails(crtDetail);


            //Process Details (query pack api)
            List<String> parameterDetail = getParameterDetails(boomiInformation.getEnvironmentID(),boomiInformation.getAccountId(),request.getProcessName());
            response.setParameterDetails(parameterDetail);


            return response;
        }



//        else {
//            throw new TPApiServiceException(ErrorMessagePropertyConstants.INTEGRATION_PACK_NOT_FOUND_ERROR, HttpStatus.NOT_FOUND);
//        }
//        if(id==valid)


//        ResponseObject response = new ResponseObject();
//        response.setCrtDetails(["crtConfig"]);
//        else
//          exception
        // Parameter Type( falcon)
        //Need to be returned exception.
        return null;
    }

    public String queryIntegrationPackInstanceId(String integrationPackName, String accountId) {
        QueryIPackRequest queryRequest = integrationLibraryService.createSimpleQueryExpression(
                List.of(integrationPackName), IhubPropertyConstants.INTEGRATION_PACK_NAME, IhubPropertyConstants.BoomiOpretorEnum.EQUALS.toString());
        logger.info("METHOD START- [queryIntegrationPackInstanceId] of IntegrationPackInstanceQueryServiceImpl ");
        logger.debug("IntegrationPackInstanceQueryServiceImpl Account ID {},IPackName {}", accountId, integrationPackName);
        HttpEntity<QueryIPackRequest> entity = new HttpEntity<>(queryRequest, integrationLibraryService.getHttpHeader());
        try {
            QueryIPackResponse response = tpRetryBoomiService.thirdPartyApiCall(
                    integrationLibraryService.getUri(IhubPropertyConstants.BOOMI_IPACK_INSTANCE_QUERY_URL, accountId), entity, HttpMethod.POST, QueryIPackResponse.class);

            Optional<QueryIPackResult[]> result = Optional.ofNullable(response).filter(iPackResult -> response.getNumberOfResults() > 0)
                    .map(QueryIPackResponse::getResult);
            if (result.isPresent()) {
                logger.debug("IntegrationPackInstanceQueryServiceImpl result integrationPackInstanceId {}", result.get()[0].getId());
                return result.get()[0].getId();
            }
        } catch (HttpClientErrorException | HttpServerErrorException httpExec) {
//            throw new TPApiServiceException(ErrorMessagePropertyConstants.INTEGRATION_PACK_INSTANCE_QUERY, httpExec.getStatusCode());
        }
        return null;
    }

    public ProcessDetails getProcessDetail(RequestObject request, String accountId){

//        String processDescription = getProcessDetailDescription(request.getIPackName(),accountId);
        ProcessDetails processDetail = new ProcessDetails();
        processDetail.setProcessName(request.getProcessName());
        processDetail.setProcessType(request.getProcessType());
        processDetail.setProcessDescription(request.getProcessDescription());

        return processDetail;
    }

//    public String getProcessDetailDescription(String integrationPackName, String accountId) {
//        QueryIPackRequest queryRequest = integrationLibraryService.createSimpleQueryExpression(
//                List.of(integrationPackName), IhubPropertyConstants.NAME, IhubPropertyConstants.BoomiOpretorEnum.EQUALS.toString());
//        logger.info("METHOD START- [queryIntegrationPackInstanceId] of IntegrationPackInstanceQueryServiceImpl ");
//        logger.debug("IntegrationPackInstanceQueryServiceImpl Account ID {},IPackName {}", accountId, integrationPackName);
//        HttpEntity<QueryIPackRequest> entity = new HttpEntity<>(queryRequest, integrationLibraryService.getHttpHeader());
//        try {
//            QueryIPackResponse response = tpRetryBoomiService.thirdPartyApiCall(
//                    integrationLibraryService.getUri(IhubPropertyConstants.BOOMI_IPACK_QUERY_URL, accountId), entity, HttpMethod.POST, QueryIPackResponse.class);
//
//            Optional<QueryIPackResult[]> result = Optional.ofNullable(response).filter(iPackResult -> response.getNumberOfResults() > 0)
//                    .map(QueryIPackResponse::getResult);
//            if (result.isPresent()) {
//                logger.debug("IntegrationPackInstanceQueryServiceImpl result integrationPackInstanceId {}", result.get()[0].getDescription());
//                return result.get()[0].getDescription();
//            }
//        } catch (HttpClientErrorException | HttpServerErrorException httpExec) {
////            throw new TPApiServiceException(ErrorMessagePropertyConstants.INTEGRATION_PACK_INSTANCE_QUERY, httpExec.getStatusCode());
//        }
//        return null;
//    }
    public EnvironmentExtension getEnvironmentExtension(String environmentId, String primaryAcctId) {
        logger.info("METHOD START- [EnvironmentExtensionResponse]  environment Id {},primaryAcctId {}", environmentId,
                primaryAcctId);
        HttpEntity<String> entity = new HttpEntity<>(integrationLibraryService.getHttpHeader());
        try {
            logger.info("METHOD START- inside try block  environment Id {},primaryAcctId {}", environmentId,
                    primaryAcctId);
            EnvironmentExtension response = tpRetryBoomiService.thirdPartyApiCall(
                    integrationLibraryService.getUri(IhubPropertyConstants.ENVIRONMENT_EXTENSION.concat(environmentId), primaryAcctId),
                    entity, HttpMethod.GET, EnvironmentExtension.class);
            logger.info("EnvironmentExtensionServiceImpl response {}", response);

            if (!ObjectUtils.isEmpty(response) && !ObjectUtils.isEmpty(response.getId())) {
                return response;
            } else {
                logger.error("Exception occurred in fetching the environment extension for environmentId: '{}' "
                        + "and primaryAccountId: '{}' and ResponseId: '{}'", environmentId, primaryAcctId, response.getId());
                throw new TPApiServiceException(ErrorMessagePropertyConstants.ENVIRONMENT_EXTENSION_API, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (HttpStatusCodeException httpExcept) {
            throw new TPApiServiceException(ErrorMessagePropertyConstants.ENVIRONMENT_EXTENSION_API, httpExcept.getStatusCode());
        }

    }

//    public List<CrtDetails>  getCrtDetails(String environmentId,String primaryAcctId,String processName){
//        logger.info("METHOD START- [ArrayList<CrtDetails>]  environment Id {},primaryAcctId {}", environmentId,
//                primaryAcctId);
//        List<CrtDetails> crtDetailsList = new ArrayList<>();
//        try {
//            EnvironmentExtension environmentExtension = getEnvironmentExtension(environmentId, primaryAcctId);
//            if (environmentExtension.getCrossReferences() != null ? !environmentExtension.getCrossReferences().getCrossReference().isEmpty() : false ) {
//                List<CrossReference> crossReferences = environmentExtension.getCrossReferences().getCrossReference();
//                if (!(crossReferences.isEmpty())) {
//                    crossReferences.forEach(updatedCrossReference -> {
//                        if (updatedCrossReference.getName().substring(0,updatedCrossReference.getName().indexOf("_")).equals(processName)) {
//                            CrtDetails crtDetails = new CrtDetails();
////                                crtDetails.setCrtId(updatedCrossReference.getId());
//                            crtDetails.setCrtName(updatedCrossReference.getName());
//                            crtDetailsList.add(crtDetails);
//                        }
//                    });
//                }
//            }
//        } catch (Exception excep) {
//            logger.error("Exception occurred in fetching the CRT Details List for environmentId: '{}' "
//                    + "and primaryAccountId: '{}' and excep: '{}'", environmentId, primaryAcctId, excep.getMessage());
//            throw new TPApiServiceException(ErrorMessagePropertyConstants.CRT_DETAILS_LIST_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return crtDetailsList;
//    }

    public List<String>  getCrtDetails(String environmentId,String primaryAcctId,String processName){
        logger.info("METHOD START- [ArrayList<CrtDetails>]  environment Id {},primaryAcctId {}", environmentId,
                primaryAcctId);
        List<String> crtDetailsList = new ArrayList<>();
        try {
            EnvironmentExtension environmentExtension = getEnvironmentExtension(environmentId, primaryAcctId);
            if (environmentExtension.getCrossReferences() != null ? !environmentExtension.getCrossReferences().getCrossReference().isEmpty() : false ) {
                List<CrossReference> crossReferences = environmentExtension.getCrossReferences().getCrossReference();
                if (!(crossReferences.isEmpty())) {
                    crossReferences.forEach(updatedCrossReference -> {
                        if (updatedCrossReference.getName().substring(0,updatedCrossReference.getName().indexOf("_")).equals(processName)) {
//                            CrtDetails crtDetails = new CrtDetails();
//                                crtDetails.setCrtId(updatedCrossReference.getId());
//                            crtDetails.setCrtName();
                            crtDetailsList.add(updatedCrossReference.getName());
                        }
                    });
                }
            }
        } catch (Exception excep) {
            logger.error("Exception occurred in fetching the CRT Details List for environmentId: '{}' "
                    + "and primaryAccountId: '{}' and excep: '{}'", environmentId, primaryAcctId, excep.getMessage());
            throw new TPApiServiceException(ErrorMessagePropertyConstants.CRT_DETAILS_LIST_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return crtDetailsList;
    }

//    public List<ParameterDetails>  getParameterDetails(String environmentId, String primaryAcctId, String processName){
//        logger.info("METHOD START- [ArrayList<CrtDetails>]  environment Id {},primaryAcctId {}", environmentId,
//                primaryAcctId);
//        List<ParameterDetails> parameterDetailsList = new ArrayList<>();
//        try {
//            EnvironmentExtension environmentExtension = getEnvironmentExtension(environmentId, primaryAcctId);
//            if (environmentExtension.getProcessProperties() != null ? !environmentExtension.getProcessProperties().getProcessProperty().isEmpty() : false ) {
//                List<ProcessProperty> processProperties = environmentExtension.getProcessProperties().getProcessProperty();
//                if (!(processProperties.isEmpty())) {
//                    processProperties.forEach(updatedProcessProperties -> {
//                        if (updatedProcessProperties.getName().equals(processName) ||
//                                (updatedProcessProperties.getName().contains("_") && updatedProcessProperties.getName().substring(0,updatedProcessProperties.getName().indexOf("_")).equals(processName))) {
////                          updatedCrossReference.forEach(processPropertyValue )
//
//                            List<ProcessPropertyValue> processPropertyValues = updatedProcessProperties.getProcessPropertyValue();
//                            if(!processPropertyValues.isEmpty()){
//                                processPropertyValues.forEach( updatedProcessPropertyValues -> {
//                                    if(updatedProcessPropertyValues.getLabel() != null && !updatedProcessPropertyValues.getLabel().isEmpty()){
//                                        ParameterDetails parameterDetails = new ParameterDetails();
//                                        parameterDetails.setParameterName(updatedProcessPropertyValues.getLabel());
//                                        parameterDetailsList.add(parameterDetails);
//                                    }
//                                });
//                            }
//
//                        }
//                    });
//                }
//            }
//        } catch (Exception excep) {
//            logger.error("Exception occurred in fetching the CRT Details List for environmentId: '{}' "
//                    + "and primaryAccountId: '{}' and excep: '{}'", environmentId, primaryAcctId, excep.getMessage());
//            throw new TPApiServiceException(ErrorMessagePropertyConstants.CRT_DETAILS_LIST_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return parameterDetailsList;
//    }

    public List<String>  getParameterDetails(String environmentId, String primaryAcctId, String processName){
        logger.info("METHOD START- [ArrayList<CrtDetails>]  environment Id {},primaryAcctId {}", environmentId,
                primaryAcctId);
        List<String> parameterDetailsList = new ArrayList<>();
        try {
            EnvironmentExtension environmentExtension = getEnvironmentExtension(environmentId, primaryAcctId);
            if (environmentExtension.getProcessProperties() != null ? !environmentExtension.getProcessProperties().getProcessProperty().isEmpty() : false ) {
                List<ProcessProperty> processProperties = environmentExtension.getProcessProperties().getProcessProperty();
                if (!(processProperties.isEmpty())) {
                    processProperties.forEach(updatedProcessProperties -> {
                        if (updatedProcessProperties.getName().equals(processName) ||
                                (updatedProcessProperties.getName().contains("_") && updatedProcessProperties.getName().substring(0,updatedProcessProperties.getName().indexOf("_")).equals(processName))) {
//                          updatedCrossReference.forEach(processPropertyValue )

                            List<ProcessPropertyValue> processPropertyValues = updatedProcessProperties.getProcessPropertyValue();
                            if(!processPropertyValues.isEmpty()){
                                processPropertyValues.forEach( updatedProcessPropertyValues -> {
                                    if(updatedProcessPropertyValues.getLabel() != null && !updatedProcessPropertyValues.getLabel().isEmpty()){
//                                        ParameterDetails parameterDetails = new ParameterDetails();
//                                        parameterDetails.setParameterName(updatedProcessPropertyValues.getLabel());
                                        parameterDetailsList.add(updatedProcessPropertyValues.getLabel());
                                    }
                                });
                            }

                        }
                    });
                }
            }
        } catch (Exception excep) {
            logger.error("Exception occurred in fetching the CRT Details List for environmentId: '{}' "
                    + "and primaryAccountId: '{}' and excep: '{}'", environmentId, primaryAcctId, excep.getMessage());
            throw new TPApiServiceException(ErrorMessagePropertyConstants.CRT_DETAILS_LIST_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return parameterDetailsList;
    }

}
