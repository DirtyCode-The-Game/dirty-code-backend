package com.dirty.code.config;

import com.dirty.code.repository.GameActionRepository;
import com.dirty.code.repository.model.GameAction;
import com.dirty.code.repository.model.GameActionType;
import com.dirty.code.repository.model.SpecialAction;
import com.dirty.code.service.SimulatedAvatarService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    private final SimulatedAvatarService simulatedAvatarService;

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
        drHooLeeSheet();
        createJailActions();
    }


    private void createTrainingActions() {
        gameActionRepository.saveAll(List.of(
                GameAction.builder()
                        .type(GameActionType.TRAINING)
                        .title("Video do Deschampo")
                        .description("DESCHAMPO malemá explica algo de TI, no fim do vídeo você entendeu nada e já quer lançar o CaraLivro 2.")
                        .money(BigDecimal.valueOf(-2500))
                        .textFile("deschampo.json")
                        .actionImage("deschampo.jpg")
                        .recommendedMaxLevel(10)
                        .temporaryCharisma(3)
                        .temporaryIntelligence(1)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.TRAINING)
                        .title("TV Fonte Código")
                        .description("Chato pra caralho, mas da pra tirar alguma coisa dessa velharia. Sério nem sei se vale a pena...")
                        .money(BigDecimal.valueOf(-2500))
                        .textFile("tv_fonte_codigo.json")
                        .actionImage("tv_fonte_codigo.jpg")
                        .recommendedMaxLevel(10)
                        .temporaryCharisma(1)
                        .temporaryIntelligence(3)
                        .build(),
                    GameAction.builder()
                            .type(GameActionType.TRAINING)
                            .title("Tranquilidade Joven")
                            .description("O cara é carismatico, pena que é um completo retardado, acho melhor não seguir os conselhos de finanças dele.")
                            .money(BigDecimal.valueOf(-2500))
                            .textFile("tranquilidade_jovem.json")
                            .actionImage("tranquilidade_jovem.jpg")
                            .recommendedMaxLevel(10)
                            .temporaryCharisma(6)
                            .temporaryIntelligence(-2)
                            .build(),
                GameAction.builder()
                        .type(GameActionType.TRAINING)
                        .title("Gane Jovem Ko")
                        .description("Sério, como pode alguém esbanjar sabedoria e conhecimento com tanta falta de carisma")
                        .money(BigDecimal.valueOf(-2500))
                        .textFile("gane_jovem_ko.json")
                        .actionImage("gane_jovem_ko.jpg")
                        .recommendedMaxLevel(10)
                        .temporaryCharisma(-2)
                        .temporaryIntelligence(6)
                        .build()
                ));

        log.info("Created training actions");
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
                        .textFile("cafe_cafu.json")
                        .actionImage("cafe_cafu.jpg")
                        .build(),
                GameAction.builder()
                        .type(GameActionType.MARKET)
                        .title("Café Três Corações... de galinha")
                        .description("Um blend duvidoso que promete despertar seus instintos mais primitivos. Contém traços de penas e bico.")
                        .stamina(10)
                        .hp(-2)
                        .hpVariation(0.5)
                        .money(BigDecimal.valueOf(-15))
                        .textFile("tres_galinha.json")
                        .actionImage("tres_galinha.jpg")
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
                        .hp(10)
                        .hpVariation(0.5)
                        .money(BigDecimal.valueOf(-15))
                        .textFile("aas_infantil.json")
                        .actionImage("aas_infantil.jpg")
                        .build(),
                GameAction.builder()
                        .type(GameActionType.HOSPITAL)
                        .title("Desneuralizador")
                        .description("Ajuda você a esquecer toda bobagem que você assiste no youtube...")
                        .money(BigDecimal.valueOf(-300000))
                        .textFile("desneuralizador.json")
                        .actionImage("desneuralizador.jpg")
                        .specialAction(SpecialAction.CLEAR_TEMPORARY_STATUS)
                        .build()
                ));

        log.info("Created hospital actions");
    }

    private void drHooLeeSheet() {
        gameActionRepository.saveAll(List.of(
                GameAction.builder()
                        .type(GameActionType.SPECIAL_STATUS_SELLER)
                        .title("Seringa de Força")
                        .description("Credo vou injetar esse trosso vermelho em mim não...")
                        .money(BigDecimal.valueOf(-500000))
                        .lostHpFailure(BigInteger.valueOf(999999999L))
                        .textFile("dr_hooleesheet_str.json")
                        .actionImage("dr_hooleesheet_str.jpg")
                        .failureChance(50.0)
                        .specialAction(SpecialAction.ADD_STRENGTH)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.SPECIAL_STATUS_SELLER)
                        .title("Seringa de Inteligência")
                        .description("Um liquido azul e viscoso, parece um pouco estranho...")
                        .money(BigDecimal.valueOf(-500000))
                        .lostHpFailure(BigInteger.valueOf(999999999L))
                        .textFile("dr_hooleesheet_int.json")
                        .actionImage("dr_hooleesheet_int.jpg")
                        .failureChance(50.0)
                        .specialAction(SpecialAction.ADD_INTELLIGENCE)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.SPECIAL_STATUS_SELLER)
                        .title("Seringa de Carisma")
                        .description("Parece algodão doce liquido, credo que delicia...")
                        .money(BigDecimal.valueOf(-500000))
                        .lostHpFailure(BigInteger.valueOf(999999999L))
                        .textFile("dr_hooleesheet_cha.json")
                        .actionImage("dr_hooleesheet_cha.jpg")
                        .failureChance(50.0)
                        .specialAction(SpecialAction.ADD_CHARISMA)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.SPECIAL_STATUS_SELLER)
                        .title("Seringa de Descrição")
                        .description("Velho retardado me entregou uma seringa vazia...")
                        .money(BigDecimal.valueOf(-500000))
                        .lostHpFailure(BigInteger.valueOf(999999999L))
                        .textFile("dr_hooleesheet_ste.json")
                        .actionImage("dr_hooleesheet_ste.jpg")
                        .failureChance(50.0)
                        .specialAction(SpecialAction.ADD_STEALTH)
                        .build()
        ));

        log.info("Created Dr. Hoo Lee Sheet actions");
    }

    private void createHackingActions() {
        gameActionRepository.saveAll(List.of(
                GameAction.builder()
                        .type(GameActionType.HACKING)
                        .title("Pitbrad do CaraLivro")
                        .description("Oi vó, aqui é o Pitbrad. Manda o Pix que perdi meu cartão aqui em Hollywood.")

                        .xp(BigInteger.valueOf(5))
                        .money(BigDecimal.valueOf(250))
                        .stamina(-10)

                        .xpVariation(0.5)
                        .moneyVariation(0.5)

                        .canBeArrested(true)

                        .textFile("pitbrad_do_caralivro.json")
                        .actionImage("pitbrad_do_caralivro.jpg")
                        .failureChance(0.30)
                        .recommendedMaxLevel(10)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.HACKING)
                        .title("Vem pra Turquia sou um Sultão minha MILF gata")
                        .description("Sou sultão. Quero me casar with você. Só preciso de um Pix simbólico pra liberar o passaporte no consulado.")

                        .xp(BigInteger.valueOf(10))
                        .money(BigDecimal.valueOf(300))
                        .stamina(-10)

                        .xpVariation(0.5)
                        .moneyVariation(0.5)

                        .requiredIntelligence(1)
                        .requiredStealth(2)

                        .canBeArrested(true)

                        .textFile("sultao_da_turquia.json")
                        .actionImage("sultao_da_turquia.jpg")
                        .failureChance(0.30)
                        .recommendedMaxLevel(10)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.HACKING)
                        .title("Urubu do Pix")
                        .description("Me mande 10 dinheiros que eu te devolvo 1000 em 24h.")

                        .xp(BigInteger.valueOf(50))
                        .money(BigDecimal.valueOf(650))
                        .stamina(-25)

                        .xpVariation(0.5)
                        .moneyVariation(0.5)

                        .requiredStealth(4)

                        .canBeArrested(true)

                        .textFile("urubu_do_pix.json")
                        .actionImage("urubu_do_pix.jpg")
                        .failureChance(0.30)
                        .recommendedMaxLevel(10)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.HACKING)
                        .title("Suporte do Banco via Áudio de Zap")
                        .description("Boa tarde, aqui é do suporte. Fala sua senha em áudio pra eu ‘validar’ seu cadastro rapidinho.")

                        .xp(BigInteger.valueOf(100))
                        .money(BigDecimal.valueOf(1300))
                        .stamina(-50)

                        .xpVariation(0.5)
                        .moneyVariation(0.5)

                        .requiredIntelligence(4)
                        .requiredStealth(5)

                        .canBeArrested(true)

                        .textFile("suporte_do_banco_audio.json")
                        .actionImage("suporte_do_banco_audio.jpg")
                        .failureChance(0.30)
                        .recommendedMaxLevel(10)
                        .build(),

                //20 +
                GameAction.builder()
                        .type(GameActionType.HACKING)
                        .title("Wi-Fi Grátis (e Perigoso)")
                        .description("A cafeteria da esquina acha que 'cafezinhogratis' é uma senha segura. Você está prestes a provar que o WPA2 deles é tão forte quanto um café coado três vezes.")

                        .xp(BigInteger.valueOf(200))
                        .money(BigDecimal.valueOf(800))
                        .stamina(-25)

                        .xpVariation(0.60)
                        .moneyVariation(0.60)

                        .requiredIntelligence(8)
                        .requiredStealth(10)

                        .canBeArrested(true)

                        .textFile("wifi_cafeteria.json")
                        .actionImage("wifi_cafeteria.jpg")
                        .failureChance(0.35)
                        .recommendedMaxLevel(20)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.HACKING) 
                        .title("Mestre dos Rotworms (ElfBot)")
                        .description("Enquanto os outros jogam, você configura o script perfeito para caçar rotworms 24/7. O lucro é em gold virtual, mas o orgulho de ser um 'cheater' é real.")

                        .xp(BigInteger.valueOf(300))
                        .money(BigDecimal.valueOf(2000))
                        .stamina(-50)

                        .xpVariation(0.60)
                        .moneyVariation(0.60)

                        .requiredIntelligence(12)
                        .requiredStealth(7)

                        .canBeArrested(true)

                        .textFile("elfbot_tibia.json")
                        .actionImage("elfbot_tibia.jpg")
                        
                        .failureChance(0.35)
                        .recommendedMaxLevel(20)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.HACKING)
                        .title("SQL Injection na Padaria") 
                        .description("O site da 'Padaria do Seu Manoel' usa PHP 4 e não limpa os inputs. Um ' OR 1=1 --' e você é o novo administrador do banco de dados de pães de queijo.")

                        .xp(BigInteger.valueOf(700))
                        .money(BigDecimal.valueOf(4200))
                        .stamina(-100)

                        .xpVariation(0.60)
                        .moneyVariation(0.60)

                        .requiredIntelligence(13)
                        .requiredStealth(10)

                        .canBeArrested(true)

                       
                        .textFile("sql_injection_local.json")
                        .actionImage("sql_injection_local.jpg")
                        .failureChance(0.35)
                        .recommendedMaxLevel(20)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.HACKING) 
                        .title("Hacker de Low-Stakes (YouTube)")
                        .description("O alvo é um ex-participante de reality show que faz unboxing de recebidos. O plano? Um e-mail de 'parceria' fake para sequestrar o canal e postar vídeos de Blaze.")
                        
                        .xp(BigInteger.valueOf(1000))
                        .money(BigDecimal.valueOf(8000))
                        .stamina(-150)

                        .xpVariation(0.60)
                        .moneyVariation(0.60)

                        .requiredIntelligence(8)
                        .requiredStealth(15)

                        .canBeArrested(true)

                        .textFile("youtube_subcelebridade.json")
                        .actionImage("youtube_subcelebridade.jpg")
                        .failureChance(0.35)
                        .recommendedMaxLevel(20)
                        .build()
        ));
        log.info("Created hacking actions");
    }

    private void createWorkActions() {
        gameActionRepository.saveAll(List.of(
                GameAction.builder()
                        .type(GameActionType.WORK)
                        .title("Ajustar canais de tv do pai")
                        .description("O pai esta desesperado, a tv não esta funcionando.")

                        .xp(BigInteger.valueOf(100))
                        .money(BigDecimal.valueOf(20))
                        .stamina(-10)

                        .xpVariation(0.3)
                        .moneyVariation(0.3)

                        .lostHpFailure(BigInteger.valueOf(5))
                        .lostHpFailureVariation(0.2)

                        .textFile("tv_do_pai.json")
                        .actionImage("tv_do_pai.jpg")
                        .failureChance(0.15)
                        .recommendedMaxLevel(10)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.WORK)
                        .title("Recuperar senha do CaraLivro da mãe")
                        .description("A mãe esqueceu a senha do CaraLivro de novo, como ela vai fofocar da vida da Cleude assim?")

                        .xp(BigInteger.valueOf(200))
                        .money(BigDecimal.valueOf(30))
                        .stamina(-10)

                        .xpVariation(0.3)
                        .moneyVariation(0.3)

                        .requiredIntelligence(4)
                        .requiredCharisma(2)

                        .lostHpFailure(BigInteger.valueOf(20))
                        .lostHpFailureVariation(0.2)

                        .textFile("caralivro_mae.json")
                        .actionImage("caralivro_mae.jpg")
                        .failureChance(0.15)
                        .recommendedMaxLevel(10)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.WORK)
                        .title("Revolta das impressoras")
                        .description("Sua impressora começou a cuspir folhas escrito \"foda-se\" de um jeito nisso!")

                        .xp(BigInteger.valueOf(500))
                        .money(BigDecimal.valueOf(30))
                        .stamina(-20)

                        .xpVariation(0.3)
                        .moneyVariation(0.3)

                        .requiredIntelligence(3)
                        .requiredCharisma(5)

                        .lostHpFailure(BigInteger.valueOf(20))
                        .lostHpFailureVariation(0.2)

                        .textFile("revolta_impressoras.json")
                        .actionImage("revolta_impressoras.jpg")
                        .failureChance(0.15)
                        .recommendedMaxLevel(10)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.WORK)
                        .title("Animador de festas profissional!")
                        .description("Prepare o pendrive aquele biquinho na festa como DJ deu certo, hoje o Alok vai chorar no banho!")

                        .xp(BigInteger.valueOf(800))
                        .money(BigDecimal.valueOf(150))
                        .stamina(-25)

                        .xpVariation(0.3)
                        .moneyVariation(0.3)

                        .requiredIntelligence(4)
                        .requiredCharisma(6)

                        .lostHpFailure(BigInteger.valueOf(40))
                        .lostHpFailureVariation(0.2)

                        .textFile("dj.json")
                        .actionImage("dj.jpg")
                        .failureChance(0.15)
                        .recommendedMaxLevel(10)
                        .build(),

                //20+
                GameAction.builder() //passar cafe
                        .type(GameActionType.WORK)
                        .title("Barista de Emergência")
                        .description("O servidor caiu? Não importa. O café acabou? CAOS TOTAL. Você é o único capaz de operar a máquina italiana de 1990 que exige um sacrifício humano para funcionar.")

                        .xp(BigInteger.valueOf(900))
                        .money(BigDecimal.valueOf(300))
                        .stamina(-20)

                        .xpVariation(0.3)
                        .moneyVariation(0.3)

                        .requiredIntelligence(5)
                        .requiredCharisma(10)

                        .lostHpFailure(BigInteger.valueOf(40))
                        .lostHpFailureVariation(0.5)

                        .textFile("barista_emergencia.json")
                        .actionImage("barista_emergencia.jpg")
                        .failureChance(0.20)
                        .recommendedMaxLevel(20)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.WORK)
                        .title("Sobrevivente de 'Daily' Infinita")
                        .description("A reunião era pra durar 15 minutos em pé. Já se passaram 2 horas, todos estão sentados no chão e o PO está explicando o sentido da vida.")

                        .xp(BigInteger.valueOf(2000))
                        .money(BigDecimal.valueOf(700))
                        .stamina(-40)

                        .xpVariation(0.3)
                        .moneyVariation(0.3)

                        .requiredIntelligence(7)
                        .requiredCharisma(10)

                        .lostHpFailure(BigInteger.valueOf(50))
                        .lostHpFailureVariation(0.5)

                        .textFile("daily_infinita.json")
                        .actionImage("daily_infinita.jpg")
                        .failureChance(0.20)
                        .recommendedMaxLevel(20)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.WORK)
                        .title("Fiscal de Digitação")
                        .description("Você e outro dev olhando para a mesma tela. Um digita, o outro julga cada erro de sintaxe em silêncio. É tipo dirigir com o sogro no carona.")

                        .xp(BigInteger.valueOf(2600))
                        .money(BigDecimal.valueOf(900))
                        .stamina(-50)

                        .xpVariation(0.3)
                        .moneyVariation(0.3)

                        .requiredIntelligence(10)
                        .requiredCharisma(10)

                        .lostHpFailure(BigInteger.valueOf(50))
                        .lostHpFailureVariation(0.5)

                        .textFile("pair_programming.json")
                        .actionImage("pair_programming.jpg")
                        .failureChance(0.20)
                        .recommendedMaxLevel(20)
                        .build(),
                GameAction.builder()
                        .type(GameActionType.WORK)
                        .title("Arqueologia Digital: Migrando o Caos")
                        .description("O plano é levar o sistema de 1985 para a nuvem. Na prática, você está só empurrando o lixo pra baixo do tapete de outra pessoa (a Amazon).")

                        .xp(BigInteger.valueOf(2800))
                        .money(BigDecimal.valueOf(1000))
                        .stamina(-30)

                        .xpVariation(0.3)
                        .moneyVariation(0.3)

                        .requiredIntelligence(17)
                        .requiredCharisma(8)

                        .lostHpFailure(BigInteger.valueOf(80))
                        .lostHpFailureVariation(0.5)

                        .textFile("arqueologia_digital.json")
                        .actionImage("arqueologia_digital.jpg")
                        .failureChance(0.20)
                        .recommendedMaxLevel(20)
                        .build()
        ));

        log.info("Created work actions");
    }

    private void createJailActions() {
        gameActionRepository.saveAll(List.of(
                GameAction.builder()
                        .type(GameActionType.JAIL)
                        .title("Trabalho Voluntário")
                        .description("E ai seu otário, você pode ajudar a gente a limpar a pracinha e em troca vamos reduzir um pouco seu nível de procurado...")
                        .stamina(-50)
                        .hp(-50)
                        .specialAction(SpecialAction.VOLUNTARY_WORK)
                        .textFile("trabalho_voluntario.json")
                        .actionImage("trabalho_voluntario.jpg")
                        .build()
        ));
        log.info("Created jail actions");
    }
}
