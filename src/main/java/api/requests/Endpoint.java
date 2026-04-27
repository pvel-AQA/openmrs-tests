package api.requests;

import api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Endpoint {
    SESSION(
            "/session",
            BaseModel.class,
            RetrieveSessionResponse.class
    ),
    IDENTIFIER_SOURCE(
            "/idgen/identifiersource",
            BaseModel.class,
            IdentifierSource.class
    ),
    IDENTIFIER(
            "/idgen/identifiersource/{uuid}/identifier",
            BaseModel.class,
            IdentifierResponse.class
    ),
    LOCATION(
            "/location",
            BaseModel.class,
            LocationResponse.class
    ),
    PATIENT(
            "/patient/",
            CreatePatientRequest.class,
            CreatePatientResponse.class
    ),
    PATIENT_SEARCH(
            "/patient/{uuid}",
            BaseModel.class,
            CreatePatientResponse.class
    ),
    VISIT(
            "/visit",
            CreateVisitRequest.class,
            CreateVisitResponse.class
    ),
    VISIT_SEARCH(
            "/visit/{uuid}",
            BaseModel.class,
            CreateVisitResponse.class
    ),
    VISIT_BY_PATIENT(
            "/visit",
            BaseModel.class,
            CreateVisitResponse.class
    ),
    VISIT_TYPE(
            "/visittype",
            BaseModel.class,
            VisitTypeResponse.class
    ),
    VISIT_TYPE_BY_UUID(
            "/visittype/{uuid}",
            BaseModel.class,
            VisitTypeResponse.class
    ),
    VISIT_ATTRIBUTE_TYPE(
            "/visitattributetype",
            VisitAttributeType.class,
            VisitAttributeType.class
     ),
    PATIENT_UPDATE(
            "/patient/{uuid}/identifier",
            CreatePatientRequest.class,
            CreatePatientResponse.class
    );

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;

}
