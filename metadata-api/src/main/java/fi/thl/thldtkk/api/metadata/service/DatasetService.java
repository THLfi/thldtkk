package fi.thl.thldtkk.api.metadata.service;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class DatasetService implements Service<Dataset> {

  private TermedClient termed;

  @Autowired
  public DatasetService(TermedClient termed) {
    this.termed = termed;
  }

  @Override
  public Stream<Dataset> query() {
    return termed.query("DataSet").stream().map(Dataset::new);
  }

  @Override
  public Optional<Dataset> get(UUID id) {
    return termed.getTree("DataSet", id, "*").map(Dataset::new);
  }

  @Override
  public Dataset save(Dataset dataset) {
    List<Node> nodes = new ArrayList<>();
    nodes.add(dataset.toNode());

    // save dependent nodes
    dataset.getPopulation().ifPresent(v -> nodes.add(v.toNode()));
    dataset.getInstanceVariables().forEach(v -> nodes.add(v.toNode()));

    termed.save(nodes);
    return dataset;
  }

  @Override
  public void delete(UUID id) {
    Dataset dataset = get(id).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));

    List<Node> nodes = new ArrayList<>();
    nodes.add(dataset.toNode());

    // delete dependent nodes
    dataset.getPopulation().ifPresent(v -> nodes.add(v.toNode()));
    dataset.getInstanceVariables().forEach(v -> nodes.add(v.toNode()));

    termed.delete(nodes);
  }

}
