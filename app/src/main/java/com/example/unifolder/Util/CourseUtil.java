package com.example.unifolder.Util;

import android.content.Context;

import com.example.unifolder.R;

public class CourseUtil {
    public static String[] getAvailableCourses(Context c, String macroArea) {
        // Simula il recupero dei corsi disponibili per la macroarea selezionata
        // Questo può essere un'implementazione reale che interroga un backend o un'altra fonte di dati
        // Qui, per semplicità, viene restituito un array fisso di esempio
        if (macroArea.equals(c.getString(R.string.economics)))
            return new String[]{"ECONOMIA DELLE BANCHE, DELLE ASSICURAZIONI E DEGLI INTERMEDIARI FINANZIARI [E1803M]",
                    "ECONOMIA E AMMINISTRAZIONE DELLE IMPRESE [E1802M]", "ECONOMIA E COMMERCIO [E3301M]",
                    "ECONOMIA, ANALISI DEI DATI E MANAGEMENT [E3303M]",
                    "MARKETING, COMUNICAZIONE AZIENDALE E MERCATI GLOBALI [E1801M]",
                    "SCIENZE STATISTICHE ED ECONOMICHE [E4101B]", "STATISTICA E GESTIONE DELLE INFORMAZIONI [E4102B]",
                    "ECONOMIA AZIENDALE E MANAGEMENT [E1805M]",
                    "BIOSTATISTICA [F8203B]", "ECONOMIA DEL TURISMO [F7601M]", "ECONOMIA E FINANZA [F1601M]",
                    "INTERNATIONAL ECONOMICS - ECONOMIA INTERNAZIONALE [F5602M]",
                    "MANAGEMENT E DESIGN DEI SERVIZI [F6302N]", "MARKETING E MERCATI GLOBALI [F7702M]",
                    "SCIENZE ECONOMICO-AZIENDALI [F7701M]", "SCIENZE STATISTICHE ED ECONOMICHE [F8204B]"};
        else if (macroArea.equals(c.getString(R.string.law)))
            return new String[]{"SCIENZE DEI SERVIZI GIURIDICI [E1401A]","DIRITTO DELLE ORGANIZZAZIONI PUBBLICHE E PRIVATE [FSG01A]",
                    "HUMAN-CENTERED ARTIFICIAL INTELLIGENCE [F551MI]"};
        else if (macroArea.equals(c.getString(R.string.medicine)))
            return new String[]{"FISIOTERAPIA [I0201D]", "IGIENE DENTALE [I0301D]", "INFERMIERISTICA [I0101D]",
                    "OSTETRICIA [I0102D]", "TECNICHE DI LABORATORIO BIOMEDICO [I0302D]",
                    "TECNICHE DI RADIOLOGIA MEDICA, PER IMMAGINI E RADIOTERAPIA [I0303D]",
                    "TERAPIA DELLA NEURO E PSICOMOTRICITÀ DELL'ETÀ EVOLUTIVA [I0202D]",
                    "BIOTECNOLOGIE MEDICHE [F0901D]", "SCIENZE INFERMIERISTICHE E OSTETRICHE [K0101D]"};
        else if (macroArea.equals(c.getString(R.string.psychology)))
            return new String[]{"SCIENZE E TECNICHE PSICOLOGICHE [E2401P]", "SCIENZE PSICOSOCIALI DELLA COMUNICAZIONE [E2004P]",
                    "INTERPRETARIATO E TRADUZIONE IN LINGUA DEI SEGNI ITALIANA (LIS) E LINGUA DEI SEGNI ITALIANA TATTILE (LIST) [E2005P]",
                    "APPLIED EXPERIMENTAL PSYCHOLOGICAL SCIENCES [F5105P]", "NEUROPSICOLOGIA E NEUROSCIENZE COGNITIVE [F5108P]",
                    "PSICOLOGIA CLINICA [F5107P]", "PSICOLOGIA CLINICA E NEUROPSICOLOGIA NEL CICLO DI VITA [F5104P]",
                    "PSICOLOGIA DELLO SVILUPPO E DEI PROCESSI EDUCATIVI [F5103P]",
                    "PSICOLOGIA SOCIALE, ECONOMICA E DELLE DECISIONI [F5106P]",
                    "TEORIA E TECNOLOGIA DELLA COMUNICAZIONE [F9201P] (INTERDIPARTIMENTALE CON INFORMATICA)"};
        else if (macroArea.equals(c.getString(R.string.education)))
            return new String[]{"COMUNICAZIONE INTERCULTURALE [E2001R]",
                    "SCIENZE DELL'EDUCAZIONE [E1901R]", "FORMAZIONE E SVILUPPO DELLE RISORSE UMANE [F5701R]",
                    "SCIENZE ANTROPOLOGICHE ED ETNOLOGICHE [F0101R]", "SCIENZE PEDAGOGICHE [F8501R]",
                    "LINGUAGGI ARTISTICI PER LA FORMAZIONE [F5702R]"};
        else if (macroArea.equals(c.getString(R.string.science)))
            return new String[]{"ARTIFICIAL INTELLIGENCE [E311PV]",
                    "BIOTECNOLOGIE [E0201Q]","FISICA [E3001Q]","INFORMATICA [E3101Q]",
                    "MATEMATICA [E3501Q]", "OTTICA E OPTOMETRIA [E3002Q]", "SCIENZA DEI MATERIALI [E2701Q]",
                    "SCIENZA E NANOTECNOLOGIA DEI MATERIALI [ESM01Q]", "SCIENZE BIOLOGICHE [E1301Q]",
                    "SCIENZE E TECNOLOGIE CHIMICHE [E2702Q]", "SCIENZE E TECNOLOGIE GEOLOGICHE [E3401Q]",
                    "SCIENZE E TECNOLOGIE PER L'AMBIENTE [E3201Q]",
                    "ARTIFICIAL INTELLIGENCE FOR SCIENCE AND TECHNOLOGY [F9102Q]",
                    "ASTROFISICA E FISICA DELLO SPAZIO [F5801Q]", "ASTROPHYSICS AND SPACE PHYSICS [F5802Q]",
                    "BIOLOGIA [F0601Q]", "BIOTECNOLOGIE INDUSTRIALI [F0802Q]", "DATA SCIENCE [F9101Q]",
                    "DATA SCIENCE [FDS01Q]", "FISICA [F1701Q]", "INFORMATICA [F1801Q]",
                    "MARINE SCIENCES [F7502Q]", "MATEMATICA [F4001Q]", "MATERIALS SCIENCE [F5302Q]",
                    "MATERIALS SCIENCE AND NANOTECHNOLOGY [FSM01Q]", "SCIENZA DEI MATERIALI [F5301Q]",
                    "SCIENZE E TECNOLOGIE CHIMICHE [F5401Q]", "SCIENZE E TECNOLOGIE GEOLOGICHE [F7401Q]",
                    "SCIENZE E TECNOLOGIE PER L'AMBIENTE E IL TERRITORIO [F7501Q]",
                    "TEORIA E TECNOLOGIA DELLA COMUNICAZIONE [F9201P]"};
        else if (macroArea.equals(c.getString(R.string.sociology)))
            return new String[]{"SCIENZE DEL TURISMO E COMUNITÀ LOCALE [E1501N]",
                    "SCIENZE DELL'ORGANIZZAZIONE [E1601N]", "SERVIZIO SOCIALE [E3901N]",
                    "SOCIOLOGIA [E4001N]", "ANALISI DEI PROCESSI SOCIALI [F8802N]",
                    "MANAGEMENT E DESIGN DEI SERVIZI [F6302N]", "PROGRAMMAZIONE E GESTIONE DELLE POLITICHE E DEI SERVIZI SOCIALI [F8701N]",
                    "SICUREZZA, DEVIANZA E GESTIONE DEI RISCHI [F8803N]", "TURISMO, TERRITORIO E SVILUPPO LOCALE [F4901N]"};
        else return new String[]{};
    }

    public static String[] initMacroAreas(Context context) {
        return new String[]{context.getString(R.string.economics), context.getString(R.string.law),
                context.getString(R.string.medicine), context.getString(R.string.psychology),
                context.getString(R.string.education), context.getString(R.string.science),
                context.getString(R.string.sociology)};
    }
}
