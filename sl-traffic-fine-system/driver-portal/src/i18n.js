import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'

i18n.use(initReactI18next).init({
  resources: {
    en: {
      translation: {
        title: 'Traffic Fine Payment',
        step1: 'Lookup Fine',
        step2: 'Payment Details',
        step3: 'Confirmation',
        refNumber: 'Reference Number',
        categoryId: 'Category ID',
        lookupBtn: 'Find Fine',
        fineAmount: 'Fine Amount',
        vehicleNo: 'Vehicle Number',
        district: 'District',
        payerName: 'Your Full Name',
        payerNic: 'NIC Number',
        paymentMethod: 'Payment Method',
        payBtn: 'Pay Now',
        success: 'Payment Successful!',
        downloadReceipt: 'Download Receipt',
        alreadyPaid: 'This fine has already been paid.',
        notFound: 'Fine not found. Check the reference number and category.',
        loading: 'Loading...',
      }
    },
    si: {
      translation: {
        title: 'රථ වාහන දඩ ගෙවීම',
        step1: 'දඩය සොයන්න',
        step2: 'ගෙවීම් විස්තර',
        step3: 'තහවුරු කිරීම',
        refNumber: 'යොමු අංකය',
        categoryId: 'කාණ්ඩ හැඳුනුම',
        lookupBtn: 'දඩය සොයන්න',
        fineAmount: 'දඩ මුදල',
        vehicleNo: 'රථ වාහන අංකය',
        district: 'දිස්ත්‍රිය',
        payerName: 'ඔබේ සම්පූර්ණ නම',
        payerNic: 'ජාතික හැඳුනුම්පත් අංකය',
        paymentMethod: 'ගෙවීම් ක්‍රමය',
        payBtn: 'දැන් ගෙවන්න',
        success: 'ගෙවීම සාර්ථකයි!',
        downloadReceipt: 'රිසිට්පත බාගන්න',
        alreadyPaid: 'මෙම දඩය දැනටමත් ගෙවා ඇත.',
        notFound: 'දඩය හමු නොවීය.',
        loading: 'පූරණය වෙමින්...',
      }
    },
    ta: {
      translation: {
        title: 'போக்குவரத்து அபராதம் செலுத்துதல்',
        step1: 'அபராதம் தேடுக',
        step2: 'கட்டண விவரங்கள்',
        step3: 'உறுதிப்படுத்தல்',
        refNumber: 'குறிப்பு எண்',
        categoryId: 'வகை ID',
        lookupBtn: 'அபராதம் கண்டறி',
        fineAmount: 'அபராத தொகை',
        vehicleNo: 'வாகன எண்',
        district: 'மாவட்டம்',
        payerName: 'உங்கள் முழு பெயர்',
        payerNic: 'NIC எண்',
        paymentMethod: 'கட்டண முறை',
        payBtn: 'இப்போது செலுத்துக',
        success: 'கட்டணம் வெற்றிகரமாக!',
        downloadReceipt: 'ரசீது பதிவிறக்கம்',
        alreadyPaid: 'இந்த அபராதம் ஏற்கனவே செலுத்தப்பட்டது.',
        notFound: 'அபராதம் கிடைக்கவில்லை.',
        loading: 'ஏற்றுகிறது...',
      }
    }
  },
  lng: 'en',
  fallbackLng: 'en',
  interpolation: { escapeValue: false }
})

export default i18n
