package com.dirty.code.config;

import com.dirty.code.repository.GameActionRepository;
import com.dirty.code.repository.model.GameAction;
import com.dirty.code.repository.model.GameActionType;
import com.dirty.code.repository.model.SpecialAction;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final com.dirty.code.service.SimulatedAvatarService simulatedAvatarService;

    @Value("${firebase.enabled:false}")
    private boolean firebaseEnabled;

    @PostConstruct
    public void initializeGameData() {
        log.info("Initializing game data... (Firebase enabled: {})", firebaseEnabled);

        initializeGameActions();

        if (!firebaseEnabled) {
            simulatedAvatarService.initializeSimulatedAvatars();
            simulatedAvatarService.addSimulatedInitialMessages();
        } else {
            log.info("Skipping simulated avatars initialization because Firebase is enabled.");
        }

        log.info("Game data initialization completed!");
    }

    private void initializeGameActions() {
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
    }


    private void createHackingActions() {
        gameActionRepository.saveAll(List.of(
                GameAction.builder()
                        .type(GameActionType.HACKING)
                        .title("Pitbrad do CaraLivro")
                        .description("Oi vó, aqui é o Pitbrad. Manda o Pix que perdi meu cartão aqui em Hollywood.")
                        .stamina(-10)
                        .hp(0)
                        .hpVariation(0.0)
                        .money(BigDecimal.valueOf(1000))
                        .moneyVariation(0.5)
                        .xp(100)
                        .xpVariation(0.5)
                        .requiredStrength(0)
                        .requiredIntelligence(0)
                        .requiredCharisma(0)
                        .requiredStealth(0)
                        .canBeArrested(true)
                        .lostHpFailure(0)
                        .lostHpFailureVariation(0.0)
                        .textFile("pitbrad_do_caralivro.json")
                        .actionImage("pitbrad_do_caralivro.jpg")
                        .failureChance(0.30)
                        .recommendedMaxLevel(10)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.HACKING)
                        .title("Vem pra Turquia sou um Sultão minha MILF gata")
                        .description("Sou sultão. Quero me casar with você. Só preciso de um Pix simbólico pra liberar o passaporte no consulado.")
                        .stamina(-10)
                        .hp(0)
                        .hpVariation(0.0)
                        .money(BigDecimal.valueOf(3000))
                        .moneyVariation(0.5)
                        .xp(200)
                        .xpVariation(0.5)
                        .requiredStrength(0)
                        .requiredIntelligence(1)
                        .requiredCharisma(0)
                        .requiredStealth(2)
                        .canBeArrested(true)
                        .lostHpFailure(0)
                        .lostHpFailureVariation(0.0)
                        .textFile("sultao_da_turquia.json")
                        .actionImage("sultao_da_turquia.jpg")
                        .failureChance(0.30)
                        .recommendedMaxLevel(10)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.HACKING)
                        .title("Urubu do Pix")
                        .description("Me mande 10 dinheiros que eu te devolvo 1000 em 24h.")
                        .stamina(-25)
                        .hp(0)
                        .hpVariation(0.0)
                        .money(BigDecimal.valueOf(5000))
                        .moneyVariation(0.5)
                        .xp(500)
                        .xpVariation(0.5)
                        .requiredStrength(0)
                        .requiredIntelligence(0)
                        .requiredCharisma(0)
                        .requiredStealth(4)
                        .canBeArrested(true)
                        .lostHpFailure(0)
                        .lostHpFailureVariation(0.0)
                        .textFile("urubu_do_pix.json")
                        .actionImage("urubu_do_pix.jpg")
                        .failureChance(0.30)
                        .recommendedMaxLevel(10)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.HACKING)
                        .title("Suporte do Banco via Áudio de Zap")
                        .description("Boa tarde, aqui é do suporte. Fala sua senha em áudio pra eu ‘validar’ seu cadastro rapidinho.")
                        .stamina(-50)
                        .hp(0)
                        .hpVariation(0.0)
                        .money(BigDecimal.valueOf(7000))
                        .moneyVariation(0.65)
                        .xp(1000)
                        .xpVariation(0.65)
                        .requiredStrength(0)
                        .requiredIntelligence(4)
                        .requiredCharisma(1)
                        .requiredStealth(5)
                        .canBeArrested(true)
                        .lostHpFailure(0)
                        .lostHpFailureVariation(0.0)
                        .textFile("suporte_do_banco_audio.json")
                        .actionImage("suporte_do_banco_audio.jpg")
                        .failureChance(0.30)
                        .recommendedMaxLevel(10)
                        .build()
        ));
        log.info("Created hacking actions");
    }


    private void createTrainingActions() {
        gameActionRepository.saveAll(List.of(
                GameAction.builder()
                        .type(GameActionType.TRAINING)
                        .title("Video do Deschampo")
                        .description("DESCHAMPO malemá explica algo de TI, no fim do vídeo você entendeu nada e já quer lançar o CaraLivro 2.")
                        .stamina(0)
                        .hp(0)
                        .hpVariation(0.0)
                        .money(BigDecimal.valueOf(-2500))
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
                        .textFile("deschampo.json")
                        .actionImage("deschampo.jpg")
                        .failureChance(0.0)
                        .recommendedMaxLevel(10)
                        .temporaryCharisma(3)
                        .temporaryIntelligence(1)
                        .temporaryStealth(0)
                        .temporaryStrength(0)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.TRAINING)
                        .title("TV Fonte Código")
                        .description("Chato pra caralho, mas da pra tirar alguma coisa dessa velharia. Sério nem sei se vale a pena...")
                        .stamina(0)
                        .hp(0)
                        .hpVariation(0.0)
                        .money(BigDecimal.valueOf(-2500))
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
                        .textFile("tv_fonte_codigo.json")
                        .actionImage("tv_fonte_codigo.jpg")
                        .failureChance(0.0)
                        .recommendedMaxLevel(10)
                        .temporaryCharisma(1)
                        .temporaryIntelligence(3)
                        .temporaryStealth(0)
                        .temporaryStrength(0)
                        .build(),
                    GameAction.builder()
                            .type(GameActionType.TRAINING)
                            .title("Tranquilidade Joven")
                            .description("O cara é carismatico, pena que é um completo retardado, acho melhor não seguir os conselhos de finanças dele.")
                            .stamina(0)
                            .hp(0)
                            .hpVariation(0.0)
                            .money(BigDecimal.valueOf(-2500))
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
                            .textFile("tranquilidade_jovem.json")
                            .actionImage("tranquilidade_jovem.jpg")
                            .failureChance(0.0)
                            .recommendedMaxLevel(10)
                            .temporaryCharisma(6)
                            .temporaryIntelligence(-2)
                            .temporaryStealth(0)
                            .temporaryStrength(0)
                            .build(),
                GameAction.builder()
                        .type(GameActionType.TRAINING)
                        .title("Gane Jovem Ko")
                        .description("Sério, como pode alguém esbanjar sabedoria e conhecimento with tanta falta de carisma")
                        .stamina(0)
                        .hp(0)
                        .hpVariation(0.0)
                        .money(BigDecimal.valueOf(-2500))
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
                        .textFile("gane_jovem_ko.json")
                        .actionImage("gane_jovem_ko.jpg")
                        .failureChance(0.0)
                        .recommendedMaxLevel(10)
                        .temporaryCharisma(-2)
                        .temporaryIntelligence(6)
                        .temporaryStealth(0)
                        .temporaryStrength(0)
                        .build()
                ));

        log.info("Created training actions");
    }

    private void createWorkActions() {
        gameActionRepository.saveAll(List.of(
                GameAction.builder()
                        .type(GameActionType.WORK)
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
                        .lostHpFailure(5)
                        .lostHpFailureVariation(0.2)
                        .textFile("tv_do_pai.json")
                        .actionImage("tv_do_pai.jpg")
                        .failureChance(0.10)
                        .recommendedMaxLevel(10)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.WORK)
                        .title("Recuperar senha do CaraLivro da mãe")
                        .description("A mãe esqueceu a senha do CaraLivro de novo, como ela vai fofocar da vida da Cleude assim?")
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
                        .lostHpFailure(20)
                        .lostHpFailureVariation(0.2)
                        .textFile("caralivro_mae.json")
                        .actionImage("caralivro_mae.jpg")
                        .failureChance(0.20)
                        .recommendedMaxLevel(10)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.WORK)
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
                        .lostHpFailure(20)
                        .lostHpFailureVariation(0.2)
                        .textFile("revolta_impressoras.json")
                        .actionImage("revolta_impressoras.jpg")
                        .failureChance(0.20)
                        .recommendedMaxLevel(10)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.WORK)
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
                        .lostHpFailure(25)
                        .lostHpFailureVariation(0.2)
                        .textFile("dj.json")
                        .actionImage("dj.jpg")
                        .failureChance(0.20)
                        .recommendedMaxLevel(10)
                        .build()
        ));

        log.info("Created work actions");
    }

    private void createMarketActions() {
        gameActionRepository.saveAll(List.of(
                GameAction.builder()
                        .type(GameActionType.MARKET)
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
                        .type(GameActionType.MARKET)
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
        gameActionRepository.saveAll(List.of(
                GameAction.builder()
                        .type(GameActionType.HOSPITAL)
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
                        .build(),
                GameAction.builder()
                        .type(GameActionType.HOSPITAL)
                        .title("Desneuralizador")
                        .description("Ajuda você a esquecer toda bobagem que você assiste no youtube...")
                        .stamina(0)
                        .hp(0)
                        .hpVariation(0.0)
                        .money(BigDecimal.valueOf(-300000))
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
                        .textFile("desneuralizador.json")
                        .actionImage("desneuralizador.jpg")
                        .failureChance(0.0)
                        .specialAction(SpecialAction.CLEAR_TEMPORARY_STATUS)
                        .build()
                ));

        log.info("Created hospital actions");
    }
}
