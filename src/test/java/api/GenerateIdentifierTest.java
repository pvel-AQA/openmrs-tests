package api;

import api.models.IdentifierResponse;
import api.models.IdentifierSource;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GenerateIdentifierTest extends BaseTest{

    @Test
    public void newIdentifierCanBeGeneratedTest() {
        final String identifierSourceUuid = "8549f706-7e85-4c1d-9424-217d50a2988b";
        final String identifierTypeUuid = "05a29f94-c0ed-11e2-94be-8c13b969e334";

        IdentifierSource sourceResponse = new ValidatedCrudRequester<IdentifierSource>(
                RequestSpecs.adminSpec(),
                Endpoint.IDENTIFIER_SOURCE,
                ResponseSpecs.requestReturnsOK())
                .getAll(new CrudRequester.QueryBuilder().vEqualsFull().build(),
                        IdentifierSource.class).getFirst();

        String uuidOfIdentifierSource = sourceResponse.getUuid();
        String uuidOfIdentifierType = sourceResponse.getIdentifierType().getUuid();

        String identifier = new ValidatedCrudRequester<IdentifierResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.IDENTIFIER,
                ResponseSpecs.requestReturnsCreated()
        ).post(null, uuidOfIdentifierSource).getIdentifier();

        assertThat(uuidOfIdentifierSource).isEqualTo(identifierSourceUuid);
        assertThat(uuidOfIdentifierType).isEqualTo(identifierTypeUuid);
        assertThat(identifier).hasSize(7);

    }
}
