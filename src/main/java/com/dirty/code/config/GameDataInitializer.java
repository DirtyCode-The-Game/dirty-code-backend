package com.dirty.code.config;

import com.dirty.code.repository.GameActionRepository;
import com.dirty.code.repository.model.GameAction;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

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
        gameActionRepository.saveAll(List.of(
                GameAction.builder()
                        .type("work")
                        .title("Ajustar canais de tv do pai")
                        .description("O pai esta desesperado, a tv não esta funcionando.")
                        .stamina(-10)
                        .hp(0)
                        .hpVariation(0.0)
                        .money(BigDecimal.valueOf(20))
                        .moneyVariation(0.5)
                        .xp(10)
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
                        .failureChance(0.10)
                        .build(),
                GameAction.builder()
                        .type("work")
                        .title("Recuperar senha do BookFace da mãe")
                        .description("A mãe esqueceu a senha do BookFace de novo, como ela vai fofocar da vida da Cleude assim?")
                        .stamina(-10)
                        .hp(0)
                        .hpVariation(0.0)
                        .money(BigDecimal.valueOf(30))
                        .moneyVariation(0.5)
                        .xp(50)
                        .xpVariation(0.5)
                        .requiredStrength(0)
                        .requiredIntelligence(2)
                        .requiredCharisma(1)
                        .requiredStealth(0)
                        .canBeArrested(false)
                        .lostHpFailure(10)
                        .lostHpFailureVariation(0.2)
                        .textFile("bookface_mae.json")
                        .actionImage("bookface_mae.jpg")
                        .failureChance(0.20)
                        .build(),
                GameAction.builder()
                        .type("work")
                        .title("Revolta das impressoras")
                        .description("Sua impressora começou a cuspir folhas escrito \"foda-se\" de um jeito nisso!")
                        .stamina(-20)
                        .hp(0)
                        .hpVariation(0.0)
                        .money(BigDecimal.valueOf(5))
                        .moneyVariation(0.5)
                        .xp(100)
                        .xpVariation(0.5)
                        .requiredStrength(0)
                        .requiredIntelligence(4)
                        .requiredCharisma(0)
                        .requiredStealth(0)
                        .canBeArrested(false)
                        .lostHpFailure(10)
                        .lostHpFailureVariation(0.2)
                        .textFile("revolta_impressoras.json")
                        .actionImage("revolta_impressoras.jpg")
                        .failureChance(0.20)
                        .build(),
                GameAction.builder()
                        .type("work")
                        .title("Animador de festas profissional!")
                        .description("Prepare o pendrive aquele biquinho na festa como DJ deu certo, hoje o Alok vai chorar no banho!")
                        .stamina(-25)
                        .hp(0)
                        .hpVariation(0.0)
                        .money(BigDecimal.valueOf(150))
                        .moneyVariation(0.5)
                        .xp(200)
                        .xpVariation(0.5)
                        .requiredStrength(0)
                        .requiredIntelligence(3)
                        .requiredCharisma(5)
                        .requiredStealth(0)
                        .canBeArrested(false)
                        .lostHpFailure(15)
                        .lostHpFailureVariation(0.2)
                        .textFile("dj.json")
                        .actionImage("dj.jpg")
                        .failureChance(0.20)
                        .build()
                ));

        log.info("Created work actions");
    }

    private void createMarketActions() {
        gameActionRepository.saveAll(List.of(
                GameAction.builder()
                    .type("market")
                    .title("Café Cafú")
                    .description("Tem mais galho, casca e pedra que café nessa bosta.")
                    .stamina(5)
                    .hp(-5)
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
                    .build(),
                GameAction.builder()
                        .type("market")
                        .title("Café Três Corações... de galinha")
                        .description("Um blend duvidoso que promete despertar seus instintos mais primitivos. Contém traços de penas e bico.")
                        .stamina(10)
                        .hp(-2)
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
                        .textFile("tres_galinha.json")
                        .actionImage("tres_galinha.jpg")
                        .failureChance(0.0)
                        .build()
                ));

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
