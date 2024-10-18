const DateTimeConverter = {
    convertTimeArrToString(timeArr) {
        return `${timeArr[0].toString().padStart(2, '0')}:${timeArr[1].toString().padStart(2, '0')}`;
    },
    convertDateArrayToSlashDate(dateArr) {
        return `${dateArr[0]}/${dateArr[1].toString().padStart(2, '0')}/${dateArr[2].toString().padStart(2, '0')}`;
    },
    convertDateArrayToDate(dateArr) {
        return new Date(dateArr[0], dateArr[1] - 1, dateArr[2]);
    },
    convertDateArrayToPlainDate(dateArr) {
        return `${dateArr[0]}${dateArr[1].toString().padStart(2, '0')}${dateArr[2].toString().padStart(2, '0')}`;
    },
    convertDateToPlainDate(date) {
        const year = date.getFullYear();
        const month = (date.getMonth()+1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');

        return `${year}${month}${day}`;
    },
    convertDateToSlashDate(date) {
        const year = date.getFullYear();
        const month = (date.getMonth()+1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        return `${year}/${month}/${day}`;
    },
    reverseSlashDate(slashDate) {
        const dateArr = slashDate.split('/');
        return `${dateArr[2]}/${dateArr[1]}/${dateArr[0]}`;
    },
    convertSlashDateToPlainDate(slashDate) {
        const dateArr = slashDate.split('');
        return `${dateArr[0]}${dateArr[1]}${dateArr[2]}`;
    },
    convertPlainDateToDate(plainDate) {
        const year = plainDate.substring(0,4)
        const monthIndex = parseInt(plainDate.substring(4, 6)) - 1;
        const day = plainDate.substring(6);

        return new Date(year, monthIndex, day);
    },
    convertPlainDateToSlashDate(plainDate) {
        const date = DateTimeConverter.convertPlainDateToDate(plainDate);
        return DateTimeConverter.convertDateToSlashDate(date);
    },
    convertSlashDateToDate(date) {
        const dateArr = date.split('/');
        return new Date(parseInt(dateArr[0]), parseInt(dateArr[1])-1, parseInt(dateArr[2]));
    },
    getPlainDate(){
        return `${myDatePicker.jsCalendar._now.getFullYear()}${String(myDatePicker.jsCalendar._now.getMonth()+1).padStart(2,'0')}${String(myDatePicker.jsCalendar._now.getDate()).padStart(2,'0')}`
    }
}