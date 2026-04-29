package api.requests.steps;

import api.models.CreatePatientResponse;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;

import java.util.List;

public class PatientSteps {

    // create patients

    public static List<CreatePatientResponse> searchPatients(String searchText) {
        return new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT,
                ResponseSpecs.requestReturnsOK())
                .getWithParams(new CrudRequester.QueryBuilder().q(searchText).build(), CreatePatientResponse.class);
    }

    // delete patients
}
