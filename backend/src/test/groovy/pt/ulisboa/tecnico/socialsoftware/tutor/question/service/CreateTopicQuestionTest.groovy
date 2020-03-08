import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicQuestion
import spock.lang.Specification

class CreateTopicQuestionTest extends Specification{

    def topicQuestion

    def setUp(){
        topicQuestion = new TopicQuestion()
    }

    def "the student exists and creates a topic to a course"(){
        //TopicQuestion is created
        expect: false
    }

    def "the course does not exist"(){
        //an exception is trown
        expect: false
    }

    def "the student does not exist"(){
        //an exception is trown
        expect: false
    }

    def "the teacher does not exist"(){
        //an exception is trown
        expect: false
    }

    def "the student exists but does not belongs to the course"(){
        //an exception is trown
        expect: false
    }

    def "the teacher exists but not teach the course"(){
        //an exception is trown
        expect: false
    }

    def "the topic question already exists"(){
        //an exception is trown
        expect: false
    }

    def "the teacher justification is empty"(){
        //an exception is trown
        expect: false
    }
    
}