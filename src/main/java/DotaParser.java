import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skadistats.clarity.model.CombatLogEntry;
import skadistats.clarity.processor.gameevents.OnCombatLogEntry;
import skadistats.clarity.processor.runner.SimpleRunner;
import skadistats.clarity.source.MappedFileSource;

public class DotaParser {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String compileName(String attackerName, boolean isIllusion) {
        return attackerName != null ? attackerName + (isIllusion ? " (illusion)" : "") : "UNKNOWN";
    }

    @OnCombatLogEntry
    public void onCombatLogEntry(CombatLogEntry cle) {
        int timeInMillis = (int) (1000.0f * cle.getTimestamp());
        switch (cle.getType()) {
            case DOTA_COMBATLOG_PURCHASE:
                log.info("{}|{}|hero={}|item={}", timeInMillis, cle.getType(), cle.getTargetName(), cle.getValueName());
                break;
            case DOTA_COMBATLOG_GOLD:
                log.info("{}|{}|hero={}|delta={}", timeInMillis, cle.getType(), cle.getTargetName(), cle.getValue());
                break;
            case DOTA_COMBATLOG_XP:
//                log.info("{} {} gains {} XP",
//                        time,
//                        getTargetNameCompiled(cle),
//                        cle.getValue()
//                );
//                break;
            case DOTA_COMBATLOG_ITEM:
                // unfortunately, location for this is always (0, 0), so you can't do things like see where wards are
                // dropped
            case DOTA_COMBATLOG_GAME_STATE:
            case DOTA_COMBATLOG_DAMAGE:
            case DOTA_COMBATLOG_HEAL:
            case DOTA_COMBATLOG_MODIFIER_REMOVE:
            case DOTA_COMBATLOG_MODIFIER_ADD:
            case DOTA_COMBATLOG_ABILITY:
            case DOTA_COMBATLOG_BUYBACK:
            case DOTA_COMBATLOG_FIRST_BLOOD:
            case DOTA_COMBATLOG_TEAM_BUILDING_KILL:
            case DOTA_COMBATLOG_MULTIKILL:
            case DOTA_COMBATLOG_PLAYERSTATS:
            case DOTA_COMBATLOG_KILLSTREAK:
                // do nothing for any of these last ones
                break;
            default:
                break;
//                log.error("Ran into an unexpected combat log type, name: {}, ordinal: {}, full value: {}",
//                        cle.getType().name(), cle.getType().ordinal());
//                throw new IllegalStateException();

        }
    }

    public void run(String[] args) throws Exception {
        long tStart = System.currentTimeMillis();
        MappedFileSource source = new MappedFileSource(args[0]);
        SimpleRunner runner = new SimpleRunner(source);
        runner.runWith(this);
        long tMatch = System.currentTimeMillis() - tStart;
        log.info("total time taken: {}s", (tMatch) / 1000.0);
    }

    public static void main(String[] args) throws Exception {
        new DotaParser().run(args);
    }

}