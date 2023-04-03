package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneratorCfg {

    @XmlElement
    private String pathToWordWithQuestions;
    @XmlElement
    private int numberTickets;
    @XmlElement
    private String pathToWordWithTickets;

    @XmlElementWrapper(name = "questions")
    @XmlElement(name="block")
    private List<QuestionsCfg> questionsCfgs = null;

}
