export default class Notification {
  id: number | null = null;
  title: string = '';
  description: string = '';
  status: string = '';
  creationDate!: '';
  userId: number | null = null;

  constructor(jsonObj?: Notification) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.description = jsonObj.description;
      this.creationDate = jsonObj.creationDate;
      this.status = jsonObj.status;
      this.userId = jsonObj.userId;
    }
  }
}
