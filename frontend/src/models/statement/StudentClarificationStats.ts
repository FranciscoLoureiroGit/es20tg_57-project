export default class StudentClarificationStats{
  totalClarifications: number = 0;
  publicClarifications: number = 0;
  answeredClarifications: number = 0;
  reopenedClarifications: number = 0;
  clarificationsPerMonth: Map<number, any> | null = null;///Map of format YYYYMM, nrOfClarifications
  clarificationsPerWeek: Map<number, any> | null = null;

  constructor(jsonObj?: any ) {

    if(jsonObj != null){
      this.totalClarifications = jsonObj.totalClarifications!;
      this.publicClarifications = jsonObj.publicClarifications!;
      this.answeredClarifications = jsonObj.answeredClarifications!;
      this.reopenedClarifications = jsonObj.reopenedClarifications!;
      this.clarificationsPerMonth = new Map<number, any>();
      this.clarificationsPerWeek = new Map<number, any>();

      if(jsonObj.clarificationsPerMonth != null) {
        for (let key in jsonObj.clarificationsPerMonth!) {
          this.clarificationsPerMonth.set(Number(key), jsonObj.clarificationsPerMonth![key]);
        }
      }

      if(jsonObj.clarificationsPerWeek != null) {
        for (let key in jsonObj.clarificationsPerWeek!) {
          this.clarificationsPerWeek.set(Number(key), jsonObj.clarificationsPerWeek![key]);
        }
      }
    }
  }

}