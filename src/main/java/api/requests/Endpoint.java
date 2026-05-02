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
    PATIENT_UPDATE(
            "/patient/{uuid}",
            CreatePatientRequest.class,
            CreatePatientResponse.class
    ),
    PATIENT_DELETE(
            "/patient/{uuid}",
            CreatePatientRequest.class,
            BaseModel.class
    ),
    PERSON(
            "/person/",
            CreatePersonRequest.class,
            CreatePersonResponse.class
    ),
    PERSON_READ(
            "/person/{uuid}",
            BaseModel.class,
            CreatePersonResponse.class
    ),
    PERSON_UPDATE(
            "/person/{uuid}",
            CreatePersonRequest.class,
            CreatePersonResponse.class
    ),
    PERSON_DELETE(
            "/person/{uuid}",
            CreatePersonRequest.class,
            BaseModel.class
    ),
    PERSON_ADDRESS(
            "/person/{uuid}/address",
            BaseModel.class,
            AddressResponse.class
    );

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;

}
