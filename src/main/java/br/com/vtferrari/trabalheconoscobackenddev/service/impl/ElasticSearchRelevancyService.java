package br.com.vtferrari.trabalheconoscobackenddev.service.impl;

import br.com.vtferrari.trabalheconoscobackenddev.listener.exception.IdNotFoundException;
import br.com.vtferrari.trabalheconoscobackenddev.repository.RelevancyRepository;
import br.com.vtferrari.trabalheconoscobackenddev.repository.UserRepository;
import br.com.vtferrari.trabalheconoscobackenddev.repository.converter.RelevancyElasticsearchConverter;
import br.com.vtferrari.trabalheconoscobackenddev.repository.model.RelevancyElasticsearch;
import br.com.vtferrari.trabalheconoscobackenddev.repository.model.UserElasticsearch;
import br.com.vtferrari.trabalheconoscobackenddev.service.RelevancyService;
import br.com.vtferrari.trabalheconoscobackenddev.service.domain.Relevancy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ElasticSearchRelevancyService implements RelevancyService {

    private final RelevancyRepository relevancyRepository;
    private final UserRepository userRepository;
    private final RelevancyElasticsearchConverter relevancyElasticsearchConverter;

    @Override
    public void save(Relevancy relevancy) {
        Optional
                .ofNullable(relevancyElasticsearchConverter.convert(relevancy))
                .map(this::updateUser)
                .ifPresent(relevancyRepository::save);

    }

    private RelevancyElasticsearch updateUser(RelevancyElasticsearch relevancyElasticsearch) {
        userRepository.findById(relevancyElasticsearch.getId())
                .ifPresentOrElse(
                        userElasticsearch -> getUpdateWithPriorityLevel(userElasticsearch, relevancyElasticsearch),
                        () -> {
                            throw new IdNotFoundException("Id not found");
                        });
        return relevancyElasticsearch;
    }

    private UserElasticsearch getUpdateWithPriorityLevel(UserElasticsearch userElasticsearch, RelevancyElasticsearch relevancyElasticsearch) {
        userElasticsearch.setPriority(relevancyElasticsearch.getPriorityLevel());
        return userRepository.save(userElasticsearch);
    }
}
