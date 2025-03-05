package dev.haguel.orbistay.service;

import dev.haguel.orbistay.entity.BankCard;
import dev.haguel.orbistay.exception.BankCardNotFoundException;
import dev.haguel.orbistay.repository.BankCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankCardService {
    private final BankCardRepository bankCardRepository;

    public BankCard save(BankCard bankCard) {
        bankCard = bankCardRepository.save(bankCard);

        log.info("Bank card saved to database");
        return bankCard;
    }

    public void delete(BankCard bankCard) {
        bankCardRepository.delete(bankCard);

        log.info("Bank card deleted from database");
    }

    public BankCard findById(Long id) {
        BankCard bankCard = bankCardRepository.findById(id).orElse(null);

        if (bankCard == null) {
            log.warn("Bank card not found in database");
            throw new BankCardNotFoundException("Bank card not found in database");
        } else {
            log.info("Bank card found in database");
        }

        return bankCard;
    }
}
