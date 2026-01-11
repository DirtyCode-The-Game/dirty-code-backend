package com.dirty.code.config;

import com.dirty.code.repository.GameActionRepository;
import com.dirty.code.repository.model.GameAction;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Initializes game actions on application startup.
 * This ensures all default game actions are available in the database.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GameDataInitializer {

    private final GameActionRepository gameActionRepository;

    @PostConstruct
    public void initializeGameActions() {
        log.info("Initializing game actions...");

        long count = gameActionRepository.count();
        if (count > 0) {
            log.info("Deleting {} existing game actions...", count);
            gameActionRepository.deleteAll();
        }

        createHackingActions();
        createTrainingActions();
        createWorkActions();
        createMarketActions();
        createHospitalActions();

        log.info("Game actions initialization completed!");
    }

    private void createHackingActions() {
        gameActionRepository.save(GameAction.builder()
                .type("hacking")
                .title("Urubu do Pix")
                .description("Me mande 10 dinheiros que eu te devolvo 1000 em 24h.")
                .stamina(-25)
                .hp(0)
                .hpVariation(0.0)
                .money(BigDecimal.valueOf(50))
                .moneyVariation(0.5)
                .xp(20)
                .xpVariation(0.5)
                .requiredStrength(2)
                .requiredIntelligence(3)
                .requiredCharisma(4)
                .requiredStealth(1)
                .canBeArrested(true)
                .lostHpFailure(0)
                .lostHpFailureVariation(0.0)
                .textFile("urubu_do_pix.json")
                .actionImage("urubu_do_pix.jpg")
                .failureChance(0.2)
                .build());

        log.info("Created hacking actions");
    }

    private void createTrainingActions() {
        gameActionRepository.save(GameAction.builder()
                .type("training")
                .title("Video do Deschampo")
                .description("DESCHAMPO malemá explica algo de TI, no fim do vídeo você entendeu nada e já quer lançar o BookFace 2.")
                .stamina(-10)
                .hp(0)
                .hpVariation(0.0)
                .money(BigDecimal.valueOf(-10))
                .moneyVariation(0.0)
                .xp(10)
                .xpVariation(0.5)
                .requiredStrength(0)
                .requiredIntelligence(0)
                .requiredCharisma(0)
                .requiredStealth(0)
                .canBeArrested(false)
                .lostHpFailure(0)
                .lostHpFailureVariation(0.0)
                .textFile("deschampo.json")
                .actionImage("deschampo.jpg")
                .failureChance(0.0)
                .build());

        log.info("Created training actions");
    }

    private void createWorkActions() {
        gameActionRepository.save(GameAction.builder()
                .type("work")
                .title("Ajustar canais de tv do pai")
                .description("O pai esta desesperado, a tv não esta funcionando.")
                .stamina(-10)
                .hp(0)
                .hpVariation(0.0)
                .money(BigDecimal.valueOf(20))
                .moneyVariation(0.5)
                .xp(1000)
                .xpVariation(0.5)
                .requiredStrength(0)
                .requiredIntelligence(0)
                .requiredCharisma(0)
                .requiredStealth(0)
                .canBeArrested(false)
                .lostHpFailure(3)
                .lostHpFailureVariation(0.2)
                .textFile("tv_do_pai.json")
                .actionImage("tv_do_pai.jpg")
                .failureChance(0.15)
                .build());

        log.info("Created work actions");
    }

    private void createMarketActions() {
        gameActionRepository.save(GameAction.builder()
                .type("market")
                .title("Café Cafú")
                .description("Tem mais galho, casca e pedra que café nessa bosta.")
                .stamina(5)
                .hp(-2)
                .hpVariation(0.5)
                .money(BigDecimal.valueOf(-5))
                .moneyVariation(0.0)
                .xp(0)
                .xpVariation(0.0)
                .requiredStrength(0)
                .requiredIntelligence(0)
                .requiredCharisma(0)
                .requiredStealth(0)
                .canBeArrested(false)
                .lostHpFailure(0)
                .lostHpFailureVariation(0.0)
                .textFile("cafe_cafu.json")
                .actionImage("cafe_cafu.jpg")
                .failureChance(0.0)
                .build());

        log.info("Created market actions");
    }

    private void createHospitalActions() {
        gameActionRepository.save(GameAction.builder()
                .type("hospital")
                .title("AAS Infatil")
                .description("É docinho...")
                .stamina(0)
                .hp(10)
                .hpVariation(0.5)
                .money(BigDecimal.valueOf(-15))
                .moneyVariation(0.0)
                .xp(0)
                .xpVariation(0.0)
                .requiredStrength(0)
                .requiredIntelligence(0)
                .requiredCharisma(0)
                .requiredStealth(0)
                .canBeArrested(false)
                .lostHpFailure(0)
                .lostHpFailureVariation(0.0)
                .textFile("aas_infantil.json")
                .actionImage("aas_infantil.jpg")
                .failureChance(0.0)
                .build());

        log.info("Created hospital actions");
    }
}
