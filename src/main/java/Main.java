
import java.io.*;
import java.util.*;

import model.*;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import utils.WorkWithConfigGFiles;

public class Main {

    public static void main(String[] args) throws IOException {


        //Лист вопросов, необходимых в каждом билете
        List<BlockQuestions> blockQuestions = new ArrayList<>();

        //Парсинг конфигурационного файла
        GeneratorCfgElement generator = WorkWithConfigGFiles.unMarshalAny(GeneratorCfgElement.class, args[0]);

        //Считываем файл
        String allText = readFile(generator.getGeneratorCfg().getPathToWordWithQuestions());

        //Формируем лист необходимых вопросов
        findRequiredQuestions(allText,generator.getGeneratorCfg(),blockQuestions);

        List<Ticket> tickets = generate_random_tickets(generator.getGeneratorCfg(), blockQuestions);

        //Вывод получившихся билетов на консоль
        tickets.forEach(el->{
            System.out.println( "Билет №" + (tickets.indexOf(el)+1)+"\n" + el.toString());
        });

        //Запись в файл WORD
        writeToWord(tickets,generator.getGeneratorCfg().getPathToWordWithTickets());
    }

    private static void writeToWord(List<Ticket> tickets, String path) throws IOException {

        StringBuilder res = new StringBuilder();
        for (Ticket ticket : tickets) {
            res.append("Билет №").append(tickets.indexOf(ticket) + 1).append("\n").append(ticket.toString());
        }

        //Разбиваем с учётом абзаца
        String[] rows = String.valueOf(res).split("\n");

        try (XWPFDocument doc = new XWPFDocument()) {

            for (String row : rows) {

                // create a paragraph
                XWPFParagraph p1 = doc.createParagraph();

                //Шрифт и размер
                XWPFRun r1 = p1.createRun();
                r1.setFontSize(14);
                r1.setFontFamily("New Roman");

                //Проверка на билет
                if(row.contains("Билет")){

                    p1.setAlignment(ParagraphAlignment.CENTER);
                    r1.setBold(true);

                }else{
                    p1.setAlignment(ParagraphAlignment.LEFT);
                    r1.setBold(false);
                }


                r1.setText(row);

            }

            // save it to .docx file
            try (FileOutputStream out = new FileOutputStream(path)) {
                doc.write(out);
            }

        }

    }

    private static void findRequiredQuestions(String allText, GeneratorCfg generator, List<BlockQuestions> blockQuestions) {
        //Делим весь контент по символу окончания строки
        String[] questions = allText.split("\n");

        for (QuestionsCfg questionsCfg : generator.getQuestionsCfgs()) {

            //Список вопросов рассматриваемого блока
            String[] strings = Arrays.copyOfRange(questions, questionsCfg.getNumberStartQuestion()-1, questionsCfg.getNumberEndQuestion());

            blockQuestions.add(new BlockQuestions(strings,questionsCfg.getNumberRequiredQuestions()));

        }
    }

    private static String readFile(String path) {
        OPCPackage opcPackage = null;
        String content = null;
        try {
            opcPackage = POIXMLDocument.openPackage(path);
            XWPFDocument xwpf = new XWPFDocument(opcPackage);
            POIXMLTextExtractor poiText = new XWPFWordExtractor(xwpf);
            content = poiText.getText();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private static List<Ticket> generate_random_tickets(GeneratorCfg generator, List<BlockQuestions> blockQuestions) {

        List<Ticket> tickets = new ArrayList<>();

        //Составляем каждый билет
        for (int i = 0; i < generator.getNumberTickets(); i++) {

            //Вопросы в одном билете
            List<String>one_ticket = new ArrayList<>();

            //Двигаемся по блокам
            for (BlockQuestions blockQuestion : blockQuestions) {
                one_ticket.addAll(  generate_random_question(blockQuestion.getQuestions(), blockQuestion.getNumberRequiredQuestions()) );
            }
            tickets.add(new Ticket(one_ticket));
        }

        return tickets;

    }

    private static List<String> generate_random_question(String [] list, int number) {
        HashSet<Integer> unique_combination = new HashSet<>();
        Random random = new Random();
        while(unique_combination.size()!= number){
            unique_combination.add(random.nextInt(list.length));
        }

        //Созвращаем лист с вопросами
        List<String> rez = new ArrayList<>();
        unique_combination.forEach(el-> rez.add( list[el]) );
        return rez;
    }

}
