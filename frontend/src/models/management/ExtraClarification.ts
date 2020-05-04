export default class ExtraClarification {
  id: number | null = null;
  comment: string = '';
  commentType: string = '';
  parentClarificationId: number | null = null;
  creationDate: string = '';

  constructor(jsonObj?: ExtraClarification) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.comment = jsonObj.comment;
      this.commentType= jsonObj.commentType;
      this.parentClarificationId = jsonObj.parentClarificationId;
      this.creationDate = jsonObj.creationDate
    }
  }
}